package edu.brown.cs.jkjk.term_proj;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.jkjk.database.DBConnector;
import edu.brown.cs.jkjk.database.DataReader;
import edu.brown.cs.jkjk.database.GrouperDBManager;
import edu.brown.cs.jkjk.grouper.Group;
import edu.brown.cs.jkjk.grouper.GroupCacheHandler;
import edu.brown.cs.jkjk.grouper.GrouperWebSocket;
import edu.brown.cs.jkjk.grouper.User;
import edu.brown.cs.jkjk.grouper.UserCacheHandler;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * This is the Main class of our term project.
 *
 * @author jsoenkse, jdemanch, knambara, kvlynch
 */
public abstract class Main {

  private static final int PORT_NUM = 4567;
  private static final Gson GSON = new Gson();

  private static DBConnector grouperDB = new DBConnector();
  private static UserCacheHandler userCache = new UserCacheHandler(grouperDB);
  private static GroupCacheHandler groupCache = new GroupCacheHandler(grouperDB, userCache);
  // private static GroupControl groupController = new GroupControl(userCache, groupCache);
  private static GrouperDBManager grouperDBManager = new GrouperDBManager(userCache, groupCache,
      grouperDB);

  /**
   * Method entrypoint for CLI invocation.
   *
   * @param args Arguments passed on the command line.
   */
  public static void main(String[] args) {

    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    OptionSpec<Integer> portSpec = parser.accepts("port").withRequiredArg().ofType(Integer.class);
    OptionSet options = parser.parse(args);

    // if (options.has("gui")) {
    if (options.has(portSpec)) {
      Spark.port(options.valueOf(portSpec));
    } else {
      Spark.port(PORT_NUM);
    }
    // Connect to database
    try {
      grouperDB.connect("data/grouperDB.sqlite3");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    // Set up users and groups table in database
    try {
      grouperDBManager.deleteAllGroups();
      grouperDBManager.setUpUsersAndGroupsTable();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Run Spark server
    runSparkServer();
    // }

    // Delete groups database upon server crash/server exit
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      public void run() {
        grouperDBManager.deleteAllGroups();
      }
    }, "Shutdown-thread"));
  }

  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return 4567; // return default port if heroku-port isn't set (i.e. on localhost)
  }

  /**
   * Method to start Spark server and create Spark routes.
   */
  @SuppressWarnings("unchecked")
  public static void runSparkServer() {
    Spark.port(getHerokuAssignedPort());
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

    Spark.webSocket("/websocket", GrouperWebSocket.class);

    Spark.get("/grouper", new LandingPageHandler(), freeMarker);
    Spark.post("/grouper/dashboard", new DashboardHandler(), freeMarker);
    Spark.get("/grouper/newgroup", new NewGroupHandler(), freeMarker);
    Spark.get("/grouper/group", new GroupHandler(), freeMarker);
    Spark.post("/newuser", new NewUserHandler());
    Spark.post("/department", new DepartmentSelectHandler());
    Spark.post("/checkedClasses", new GroupDashboardHandler());
    Spark.post("/populateCourses", new CourseHandler());
    Spark.post("/createGroupInfo", new CreateGroupHandler());
    Spark.post("/grouper/group", new GroupHandler(), freeMarker);
    Spark.post("/deleteGroup", new DeleteGroupHandler());
    Spark.post("/leaveGroup", new LeaveGroupHandler());
    Spark.post("/extendGroup", new ExtendGroupHandler());
    Spark.post("/getUserGroup", new GetUserGroupHandler());
    Spark.post("/invisibility", new InvisibilityHandler());
    Spark.post("/location", new LocationHandler());
  }

  /**
   * Handler for the landing page of the site.
   *
   * @author jsoenkse
   */
  private static class LandingPageHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Grouper");
      return new ModelAndView(variables, "landing.ftl");
    }
  }

  /**
   * Handler for the dashboard page which displays groups.
   *
   * @author jsoenkse, jdemanch
   */
  private static class DashboardHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {

      QueryParamsMap qm = req.queryMap();
      String email = qm.value("login_email");
      String hash = qm.value("login_hash");

      if (grouperDB.verifyUserHash(email, hash)) {
        DataReader dr = new DataReader();
        // Get list of departments
        List<String> departmentList = dr.departments("data/departments.csv");

        Group group = groupCache.getGroup(userCache.getUser(email).getGroupID());
        String groupName = "";
        String groupLink = "";

        if (group != null) {
          groupName = group.getTitle();
          groupLink = "\"./group?gid=" + group.getGroupID() + "&uid=" + hash + "\"";
        }

        Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Your dashboard",
            "departments", departmentList, "email", email, "groupname", groupName, "grouplink",
            groupLink);
        return new ModelAndView(variables, "dashboard.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("title", "Redirecting");
        return new ModelAndView(variables, "badlogin.ftl");
      }

    }
  }

  /**
   * Handler for the page that allows you to create a new group.
   *
   * @author jsoenkse, kvlynch
   */
  private static class NewGroupHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      DataReader dr = new DataReader();
      // Get list of departments and buildings
      List<String> depts = dr.departments("data/departments.csv");
      List<String> buildings = dr.buildings("data/buildings.csv");
      // System.out.println(buildings);

      Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Create a new group",
          "departments", depts, "buildings", buildings);
      return new ModelAndView(variables, "newgroup.ftl");
    }
  }

  /**
   * Handler for creating a group form the "New Group" page on the front end. Takes all input data
   * and creates a new group.
   * 
   * @author jsoenkse, kvlynch
   */
  private static class CreateGroupHandler implements Route {

    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String dept = qm.value("department");
      String building = qm.value("building");
      String code = qm.value("course_number");
      String title = qm.value("grouptitle");
      Integer durationHours = Integer.parseInt(qm.value("duration_hours"));
      Integer durationMins = Integer.parseInt(qm.value("duration_mins"));
      Double duration = durationHours + durationMins / 60.0;
      String durString = Double.toString(duration);
      String room = qm.value("location");
      String details = qm.value("description");

      String thisUserEmail = qm.value("email");

      Map<String, String> variables = new HashMap<>();
      variables.put("department", dept);
      variables.put("building", building);
      variables.put("code", code);
      variables.put("title", title);
      variables.put("duration", durString);
      variables.put("room", room);
      variables.put("details", details);

      // Add new group info into 'groups' table of database
      grouperDBManager.addNewGroup(variables, thisUserEmail);
      int groupID = userCache.getUser(thisUserEmail).getGroupID();

      Map<String, Object> info = ImmutableMap.of("groupid", Integer.toString(groupID));

      return GSON.toJson(info);
    }
  }

  /**
   * Handles deleting group itself form db and deleting its info from each user.
   * 
   * @author Kento
   *
   */
  private static class DeleteGroupHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String mod_email = qm.value("mod");
      String hash = qm.value("hash");

      if (grouperDB.verifyUserHash(mod_email, hash)) {
        // Handles removing group and updating info in db and cache
        grouperDBManager.removeGroup(mod_email);

        Map<String, Object> info = ImmutableMap.of("msg", "success");
        return GSON.toJson(info);
      } else {
        Map<String, Object> info = ImmutableMap.of("msg", "failure");
        return GSON.toJson(info);
      }

    }
  }

  /**
   * Handler for the page that allows you to view and monitor your current group.
   */
  private static class GroupHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      Integer g_id = Integer.parseInt(req.queryParams("gid"));
      String u_id = req.queryParams("uid");

      if (!grouperDBManager.doesUserExist(u_id)) {
        Map<String, Object> variables = ImmutableMap.of("title", "Not Authorized");
        return new ModelAndView(variables, "notauthorized.ftl");
      }

      boolean isUserMod = grouperDBManager.isUserMod(u_id, g_id);

      // Handle get request for moderator group page is called
      // if (u_id.equals("modPage")) {
      if (isUserMod) {
        Group g = groupCache.getGroup(g_id);
        String groupSize = Integer.toString(g.getUsers().size());
        // @formatter:off
        Map<String, Object> variables = ImmutableMap.<String, Object>builder()
            .put("title", "Grouper - Group status")
            .put("grouptitle", g.getTitle())
            .put("groupclass", g.getCourseCode())
            .put("groupSize", groupSize)
            .put("groupdesc", g.getDetails())
            .put("groupusers", g.getUsers())
            .put("groupduration", g.getDuration())
            .put("grouproom", g.getRoom())
            .put("groupbuilding", g.getBuilding())
            .put("groupendtime", g.getEndTime())
            .put("invisiblechecked", g.getVisibility() ? "" : "checked")
            .build();
        // @formatter: on
        return new ModelAndView(variables, "group.ftl");
      }
      // Handle get request for joined group page
      Group g = groupCache.getGroup(g_id);
      String userHash = u_id;
      String userID = grouperDBManager.getUserIDFromHash(userHash);
      User u = userCache.getUser(userID);
      // Only add if group doesn't contain this user; handles refreshing group page
      if (!g.getUsers().contains(u)) {
        grouperDBManager.addUserToGroup(userID, g_id);
      }
      String groupSize = Integer.toString(g.getUsers().size());
      // @formatter:off
      Map<String, Object> variables = ImmutableMap.<String, Object>builder()
          .put("title", "Grouper - Group status")
          .put("grouptitle", g.getTitle())
          .put("groupclass", g.getCourseCode())
          .put("groupSize", groupSize)
          .put("groupdesc", g.getDetails())
          .put("groupusers", g.getUsers())
          .put("groupduration", g.getDuration())
          .put("grouproom", g.getRoom())
          .put("groupbuilding", g.getBuilding())
          .put("groupendtime", g.getEndTime())
          .put("invisiblechecked", "")
          .build();
      // @formatter: on
      return new ModelAndView(variables, "group_joined.ftl");
    }
  }

  /**
   * Handler for passing all of the classes to the front end, given the selected department
   *
   * @author jsoenkse, kvlynch
   */
  private static class DepartmentSelectHandler implements Route {

    @Override
    public String handle(Request req, Response res) throws Exception {
      QueryParamsMap qm = req.queryMap();
      String dept = qm.value("department");

      List<String> classes = grouperDBManager.getDepartmentCourses(dept);

//      Boolean expired = grouperDBManager.checkExpiredGroup();
//
//      if (expired) {
//        grouperDBManager.deleteExpiredGroups();
//      }
//      if (dept.equals("Computer Science")) {
//        classes.add("CSCI0150");
//        classes.add("CSCI0220");
//        classes.add("CSCI0320");
//      } else if (dept.equals("Biology")) {
//        classes.add("BIOL0100");
//        classes.add("BIOL1998");
//      } else if (dept.equals("Applied Math")) {
//        classes.add("APMA1650");
//        classes.add("APMA0330");
//      }
      Map<String, Object> variables = ImmutableMap.of("classes", classes);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler for obtaining group information from a list of courses.
   * 
   * @author jsoenkse, kvlynch, knambara
   */
  private static class GroupDashboardHandler implements Route {
    @Override
    public String handle(Request req, Response res) throws Exception {
      QueryParamsMap qm = req.queryMap();
      String classesUnparsed = qm.value("checked");
      String userEmail = qm.value("user");
      String userHash = qm.value("hash");
      
      int userGroupID = userCache.getUser(userEmail).getGroupID();

      // Stripping and parsing input from front end into list of just class codes
      classesUnparsed = classesUnparsed.replace("]", "");
      classesUnparsed = classesUnparsed.replace("[", "");
      classesUnparsed = classesUnparsed.replace("\"", "");
      String[] classes = classesUnparsed.split(",");
      for (String c : classes) {
        c = c.trim();
      }

      List<List<String>> groups = new ArrayList<List<String>>();
      
      // First, if the user is verified to be the moderator of some group, make that group appear first.
      for (Group g : groupCache.getCache().asMap().values()) {
        if (userGroupID == g.getGroupID() && grouperDB.verifyUserHash(userEmail, userHash)) {
          String id = Integer.toString(g.getGroupID());
          String title = g.getTitle();
          String course = g.getCourseCode();
          String size = Integer.toString(g.getUsers().size());
          String build = g.getBuilding();
          Integer trInt = (int) grouperDBManager.timeRemaining(g.getEndTime());
          String time_rem = Integer.toString(trInt);
          String end_time = g.getEndTime().toString();

          groups.add(Arrays.asList(id, title, course, size, build, time_rem, end_time));
          
          break;
        }
      }

      DataReader dr = new DataReader();
      dr.buildings("data/buildings.csv");
      Map<String, double[]> buildLocs = dr.getBuildingsLocation();

      User u = null;
      String dbUser = grouperDBManager.getUserIDFromHash(userHash);
      if (userEmail.equals(dbUser)) {
        u = userCache.getUser(dbUser);
      }


      // Get all active groups for the list of classes
      for (String c : classes) {
        for (int gid : grouperDBManager.getAllGroupIDs()) {
          Group g = groupCache.getGroup(gid);
          if (g.getCourseCode().equals(c) && g.getVisibility()) {
            String id = Integer.toString(g.getGroupID());
            String title = g.getTitle();
            String course = c;
            String size = Integer.toString(g.getUsers().size());
            String build = g.getBuilding();
            Integer trInt = (int) grouperDBManager.timeRemaining(g.getEndTime());
            String time_rem = Integer.toString(trInt);
            String end_time = g.getEndTime().toString();

            double[] buildPos = buildLocs.get(build);
            Double dist;
            if (!u.equals(null)) {
              dist = u.getDistance(buildPos);
            } else {
              dist = 0.0;
            }
            String distString = Double.toString(dist);

            groups.add(Arrays.asList(id, title, course, size, build, time_rem, end_time, distString));
          }
        }
      }

      Map<String, Object> variables = ImmutableMap.of("groups", groups);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler that returns all courses in a given department.
   * 
   * @author kvlynch
   */
  private static class CourseHandler implements Route {

    @Override
    public String handle(Request req, Response res) {
      DataReader dr = new DataReader();
      Map<String, Set<String>> courses = dr.courses("data/courses.csv");

      QueryParamsMap qm = req.queryMap();
      String dept = qm.value("department");
      Set<String> deptCourses;

      if (courses.containsKey(dept)) {
        deptCourses = courses.get(dept);
      } else {
        deptCourses = new HashSet<>();
        deptCourses.add("NONE");
      }
      
      List<String> courseList = new ArrayList<String>();
      courseList.addAll(deptCourses);
      Collections.sort(courseList);
      Map<String, Object> variables = ImmutableMap.of("dept", courseList);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler that gets the location of the user.
   *
   * @author kvlynch
   */
  private static class LocationHandler implements Route {

    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String user = qm.value("user");
      String userHash = qm.value("hash");

      String latString = qm.value("lat");
      String lonString = qm.value("lon");
      //String message = qm.value("message");
      String errorCode = qm.value("error");


      Integer errorFlag = Integer.parseInt(errorCode);
      Double lat = Double.parseDouble(latString);
      Double lon = Double.parseDouble(lonString);
      double[] position = new double[2];
      position[0] = lat;
      position[1] = lon;

      //verify the user email from post is the same as what is stored in db
      String dbUser = grouperDBManager.getUserIDFromHash(userHash);
      if (user.equals(dbUser)) {
        //if the error code is permission denied update to reflect or if the
        //position is successfully retrieved. If it times out or location
        //services are no longer available then keep the current location
        if (errorFlag == 2 || errorFlag == 0) {
          User u = userCache.getUser(dbUser);
          u.setPosition(position);
        }
      }

      return GSON.toJson("");
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.\n", templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private static final int INTERNAL_SERVER_ERROR = 500;

  /**
   * A handler to print an Exception as text into the Response.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(INTERNAL_SERVER_ERROR);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * Handles receival of newly logged-in user's information.
   * 
   * @author Kento
   */
  private static class NewUserHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String name = qm.value("name");
      String email = qm.value("email");
      String img = qm.value("img");

      if (/* email.endsWith("@brown.edu") */true) { // for testing purposes
        String hash = generateHash(32);
        grouperDBManager.addNewUser(hash, name, email, img);
        Map<String, Object> variables = ImmutableMap.of("msg", "success", "error", "", "hash",
            hash);
        return GSON.toJson(variables);
      } else {
        Map<String, Object> variables = ImmutableMap.of("msg", "error", "error",
            "Only valid brown.edu Google accounts allowed", "hash", "");
        return GSON.toJson(variables);

      }
    }
  }
  
  /**
   * Handles a mod of a group extending the time for that group.
   * 
   * @author Jeff
   */
  private static class ExtendGroupHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      
      String userHash = qm.value("hash");
      Integer groupID = Integer.parseInt(qm.value("group"));
      Integer durationHours = Integer.parseInt(qm.value("duration_hours"));
      Integer durationMins = Integer.parseInt(qm.value("duration_mins"));
      
      if (grouperDBManager.isUserMod(userHash, groupID)) {
        grouperDBManager.extendGroupTime(groupID, durationHours, durationMins);
        return "{\"status\": \"success\"}";
      }
      return "{\"status\": \"failure\"}";
    }
  }
  
  /**
   * Returns the ID of whicih group a user is in.
   * 
   * @author Jeff
   */
  private static class GetUserGroupHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      
      String userID = qm.value("user");
      String userHash = qm.value("hash");
      
      if (grouperDB.verifyUserHash(userID, userHash)) {
        return "{\"status\": \"success\",\n"
            + "\"group\": " + userCache.getUser(userID).getGroupID() + "}";
      }
      
      return "{\"status\": \"failure\", \"group\": \"undefined\"}";
    }
  }
  
  /**
   * Handler for a user leaving a group.
   * 
   * @author Kento
   */
  public static class LeaveGroupHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String userID = qm.value("user");
      int groupID = Integer.parseInt(qm.value("group"));
      
      grouperDBManager.removeUserFromGroup(userID, groupID);
      Map<String, Object> variables = ImmutableMap.of("msg", "success");
      return GSON.toJson(variables);      
    }
  }
  
  /**
   * Handles visibility of each group.
   * 
   * @author Kento  
   */
  public static class InvisibilityHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String bool = qm.value("invisibility");
      int groupID = Integer.parseInt(qm.value("gid"));
      
      if (bool.equals("on")) {
        groupCache.getGroup(groupID).setInvisible();
      } else {
        groupCache.getGroup(groupID).setVisible();
      }
      Map<String, Object> variables = ImmutableMap.of("msg", "success");
      return GSON.toJson(variables);       
    }
  }
  
  /**
   * Generates a random string of a given length using alpha-numeric characters.
   * 
   * @return The hash.
   */
  private static String generateHash(int length) {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder hash = new StringBuilder();
    Random random = new Random();
    while (hash.length() < length) {
      int index = (int) (random.nextFloat() * chars.length());
      hash.append(chars.charAt(index));
    }
    String outStr = hash.toString();
    return outStr;
  }
  

}
