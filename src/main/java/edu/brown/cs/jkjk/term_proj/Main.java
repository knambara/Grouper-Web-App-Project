package edu.brown.cs.jkjk.term_proj;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import edu.brown.cs.jkjk.grouper.Group;
import edu.brown.cs.jkjk.grouper.GroupControl;
import edu.brown.cs.jkjk.grouper.GrouperWebSocket;
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

  private static DBConnector GROUPER_DB = new DBConnector();
  private static UserCacheHandler USER_CACHE = new UserCacheHandler(GROUPER_DB);
  private static GroupControl GROUP_CONTROL = new GroupControl(GROUPER_DB);
  private static Map<Integer, Group> online_groups = new HashMap<>();

  private static String curr_user_email = "";

  /**
   * Method entrypoint for CLI invocation.
   *
   * @param args Arguments passed on the command line.
   * @throws SQLException
   */
  public static void main(String[] args) {
    OptionParser parser = new OptionParser();

    parser.accepts("gui");

    OptionSpec<Integer> portSpec = parser.accepts("port").withRequiredArg().ofType(Integer.class);

    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      if (options.has(portSpec)) {
        Spark.port(options.valueOf(portSpec));
      } else {
        Spark.port(PORT_NUM);
      }

      // Connect to database
      try {
        GROUPER_DB.connect("data/grouperDB.sqlite3");
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }

      // @formatter:off
      // Create 'users' table if it doesn't exist already
      Connection conn = GROUPER_DB.getConnection();
      String query = "CREATE TABLE IF NOT EXISTS "
          + "users(U_ID TEXT, "
          + "name TEXT, "
          + "G_ID INTEGER, "
          + "hash TEXT, "
          + "PRIMARY KEY (U_ID));";        
      try (PreparedStatement prep = conn.prepareStatement(query)) {
        prep.executeUpdate();
        prep.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
      
      // Create 'groups' table if it doesn't exist already
      Connection conn1 = GROUPER_DB.getConnection();
      // Create 'groups' table if it doesn't exist already
      String query1 = "CREATE TABLE IF NOT EXISTS "
              + "groups(G_ID INTEGER, "
              + "code TEXT, "
              + "department TEXT, "
              + "description TEXT, "
              + "duration REAL, "
              + "start TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
              + "Mod TEXT, "
              + "location TEXT, "
              + "visible INTEGER DEFAULT 1, "
              + "room TEXT, "
              + "details TEXT, "
              + "PRIMARY KEY (G_ID), "
              + "FOREIGN KEY (Mod) REFERENCES users(U_ID) ON DELETE CASCADE ON UPDATE CASCADE);";
      try (PreparedStatement prep1 = conn1.prepareStatement(query1)) {
        prep1.executeUpdate();
        prep1.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
      // @formatter:on

      // Run Spark server.
      runSparkServer();
    }
  }

  /**
   * Method to start Spark server and create Spark routes.
   */
  public static void runSparkServer() {
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
   * @author jsoenkse
   */
  private static class DashboardHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {

      QueryParamsMap qm = req.queryMap();
      String email = qm.value("login_email");
      String hash = qm.value("login_hash");

      if (GROUPER_DB.verifyUserHash(email, hash)) {
        // Hard coded for testing purposes.
        DataReader dr = new DataReader();
        List<String> departmentList = dr.departments("data/departments_sample.csv");

        Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Your dashboard",
            "departments", departmentList, "email", curr_user_email);
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
   * @author jsoenkse
   */
  private static class NewGroupHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      DataReader dr = new DataReader();
      List<String> depts = dr.departments("data/departments_sample.csv");
      List<String> buildings = new ArrayList<>();
      buildings.add("Sci Li");
      buildings.add("The Rock");
      buildings.add("Walter J Wilson");

      Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Create a new group",
          "departments", depts, "buildings", buildings);
      return new ModelAndView(variables, "newgroup.ftl");
    }
  }

  private static class CreateGroupHandler implements Route {

    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String dept = qm.value("department");
      String location = qm.value("building");
      String code = qm.value("course_number");
      String description = qm.value("grouptitle");
      Integer durationHours = Integer.parseInt(qm.value("duration_hours"));
      Integer durationMins = Integer.parseInt(qm.value("duration_mins"));
      Double duration = durationHours + durationMins / 60.0;
      String durString = Double.toString(duration);
      String room = qm.value("location");
      String details = qm.value("description");

      Map<String, String> variables = new HashMap<>();
      variables.put("department", dept);
      variables.put("location", location);
      variables.put("code", code);
      variables.put("description", description);
      variables.put("duration", durString);
      variables.put("room", room);
      variables.put("details", details);

      Group g = GROUP_CONTROL.newGroup(variables, curr_user_email);
      online_groups.put(g.getGroupID(), g);
      // String gIdString = Integer.toString(gId);

      String url = null;

      // return the URL to the new page (group view)
      try {
        url = "/grouper/group";// + URLEncoder.encode(gIdString, "UTF-8");
      } catch (Exception e) {
        System.out.println("ERROR: Could not encode url");
      }

      Map<String, Object> info = ImmutableMap.of("groupurl", url, "id",
          Integer.toString(g.getGroupID()));

      return GSON.toJson(info);
    }
  }

  /**
   * Handler for the page that allows you to view and monitor your current group.
   */
  private static class GroupHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      // Get URL
      // String id = req.params("");
      // try {
      // id = URLDecoder.decode(id, "UTF-8");
      // System.out.println(id);
      // } catch (Exception e) {
      // System.out.println("ERROR: Problem with decoding group ID.");
      // }

      Map<String, Object> info = GROUP_CONTROL.getGroupView(curr_user_email);
      System.out.println(curr_user_email);
      info.put("title", "Grouper - Group status");

      Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Group status",
          "grouptitle", info.get("grouptitle"), "groupclass", info.get("groupclass"), "groupdesc",
          info.get("groupdesc"), "groupemails", info.get("groupemails"));

      // Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Group status",
      // "grouptitle", "Group Title", "groupclass", "CLAS1234", "groupdesc",
      // "A group with a description", "groupemails", "jeffrey_demanche@brown.edu");
      return new ModelAndView(variables, "group.ftl");
    }
  }

  /**
   * Handler for passing all of the classes to the front end, given the selected department
   * 
   * FEEL FREE TO CHANGE THIS TO SUIT YOUR BACK END NEEDS!
   * 
   * @author jsoenkse
   */
  private static class DepartmentSelectHandler implements Route {

    @Override
    public String handle(Request req, Response res) throws Exception {
      QueryParamsMap qm = req.queryMap();
      String dept = qm.value("department");

      // Hard coded info in order to test front end connection
      List<String> classes = new ArrayList<String>();
      if (dept.equals("Computer Science")) {
        classes.add("CSCI0150");
        classes.add("CSCI0220");
        classes.add("CSCI0320");
      } else if (dept.equals("Biology")) {
        classes.add("BIOL0100");
        classes.add("BIOL1998");
      } else if (dept.equals("Applied Math")) {
        classes.add("APMA1650");
        classes.add("APMA0330");
      }
      Map<String, Object> variables = ImmutableMap.of("classes", classes);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler for obtaining group information from a list of courses.
   * 
   * FEEL FREE TO CHANGE THIS TO SUIT YOUR BACK END NEEDS!
   * 
   * @author jsoenkse
   */
  private static class GroupDashboardHandler implements Route {
    @Override
    public String handle(Request req, Response res) throws Exception {
      QueryParamsMap qm = req.queryMap();
      String classesUnparsed = qm.value("checked");

      // Stripping and parsing input from front end into list of just class codes
      classesUnparsed = classesUnparsed.replace("]", "");
      classesUnparsed = classesUnparsed.replace("[", "");
      classesUnparsed = classesUnparsed.replace("\"", "");
      String[] classes = classesUnparsed.split(",");
      for (String c : classes) {
        c = c.trim();
      }

      List<List<String>> groups = new ArrayList<List<String>>();

      // Hard coded examples in order to test front end
      for (String c : classes) {
        for (Group g : online_groups.values()) {
          if (g.getCourseCode().equals(c)) {
            String id = Integer.toString(g.getGroupID());
            String title = g.getDescription();
            String course = c;
            String size = Integer.toString(g.getUsers().size());
            String loc = g.getLocation();
            String time_rem = Double.toString(g.getDuration());
            groups.add(Arrays.asList(id, title, course, size, loc, time_rem));
          }
        }
        // @formatter: off
        // if (c.equals("CSCI0150")) {
        // groups.add(Arrays.asList("4", "Sketchy Meeting", "CSCI0150", "7", "Ratty", "335"));
        //
        // } else if (c.equals("CSCI0220")) {
        // groups.add(Arrays.asList("1", "Studying for Midterm", "CSCI0220", "5", "CIT", "142"));
        // groups.add(Arrays.asList("2", "Drawing Logic Circuits", "CSCI0220", "4",
        // "Science Library", "43"));
        // } else if (c.equals("CSCI0320")) {
        // groups
        // .add(Arrays.asList("3", "Talking about Appliances", "CSCI0320", "37", "CIT", "252"));
        // } else if (c.equals("BIOL0100")) {
        // groups.add(Arrays.asList("5", "Problem Set 1", "BIOL0100", "6", "Barus & Holley", "62"));
        // }
        // @formatter: on

        // loop thorough saved list of classes
      }

      Map<String, Object> variables = ImmutableMap.of("groups", groups);
      return GSON.toJson(variables);
    }
  }

  private static class CourseHandler implements Route {

    @Override
    public String handle(Request req, Response res) {
      DataReader dr = new DataReader();
      Map<String, Set<String>> courses = dr.courses("data/sample_courses.csv");

      QueryParamsMap qm = req.queryMap();
      String dept = qm.value("department");
      Set<String> deptCourses;

      if (courses.containsKey(dept)) {
        deptCourses = courses.get(dept);
      } else {
        deptCourses = new HashSet<>();
        deptCourses.add("NONE");
      }

      Map<String, Object> variables = ImmutableMap.of("dept", deptCourses);

      return GSON.toJson(variables);
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
   *
   */
  private static class NewUserHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String name = qm.value("name");
      String email = qm.value("email");
      String img = qm.value("img");

      curr_user_email = email;

      if (email.endsWith("@brown.edu")) {

        String hash = generateHash(32);

        Connection conn = GROUPER_DB.getConnection();

        String query1 = "UPDATE users SET hash=? WHERE U_ID=?;";
        String query2 = "INSERT INTO users(U_ID, name, G_ID, hash) SELECT ?, ?, ?, ? WHERE "
            + "(Select Changes() = 0);";
        try (PreparedStatement prep = conn.prepareStatement(query1)) {
          prep.setString(1, hash);
          prep.setString(2, email);

          prep.executeUpdate();
          prep.close();
        } catch (SQLException e) {
          System.err.println(e.getMessage());
        }

        try (PreparedStatement prep = conn.prepareStatement(query2)) {
          prep.setString(1, email);
          prep.setString(2, name);
          prep.setInt(3, -1);
          prep.setString(4, hash);

          prep.executeUpdate();
          prep.close();
        } catch (SQLException e) {
          System.err.println(e.getMessage());
        }

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
