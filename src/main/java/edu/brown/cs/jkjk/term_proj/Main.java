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
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.jkjk.database.DBConnector;
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
          + "PRIMARY KEY (U_ID));";        
      try (PreparedStatement prep = conn.prepareStatement(query)) {
        prep.executeUpdate();
        prep.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
      
      // Create 'groups' table if it doesn't exist already
      query = "CREATE TABLE IF NOT EXISTS "
          + "groups(G_ID INTEGER NOT NULL AUTO_INCREMENT, "
          + "code TEXT, "
          + "departmet TEXT, "
          + "description TEXT, "
          + "duration DOUBLE, "
          + "Mod TEXT, "
          + "PRIMARY KEY (G_ID), "
          + "FOREIGN KEY (Mod) REFERENCES users(U_ID) ON DELETE CASCADE ON UPDATE CASCADE);";        
      try (PreparedStatement prep = conn.prepareStatement(query)) {
        prep.executeUpdate();
        prep.close();
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

    Spark.get("/grouper", new LandingPageHandler(), freeMarker);
    Spark.get("/grouper/dashboard", new DashboardHandler(), freeMarker);
    Spark.get("/grouper/newgroup", new NewGroupHandler(), freeMarker);
    Spark.get("/grouper/group", new GroupHandler(), freeMarker);

    Spark.post("/newuser", new NewUserHandler());
    Spark.post("/department", new DepartmentSelectHandler());
    Spark.post("/checkedClasses", new GroupDashboardHandler());

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

      // Hard coded for testing purposes.
      List<String> departmentList = new ArrayList<String>();
      departmentList.add("Applied Math");
      departmentList.add("Biology");
      departmentList.add("Computer Science");

      Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Your dashboard",
          "departments", departmentList);
      return new ModelAndView(variables, "dashboard.ftl");
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
      Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Create a new group");
      return new ModelAndView(variables, "newgroup.ftl");
    }
  }

  /**
   * Handler for the page that allows you to view and monitor your current group.
   */
  private static class GroupHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Grouper - Group status",
          "grouptitle", "Group Title", "groupclass", "CLAS1234", "groupdesc",
          "A group with a description", "groupemails", "jeffrey_demanche@brown.edu");
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
        if (c.equals("CSCI0150")) {
          groups.add(Arrays.asList("4", "Sketchy Meeting", "CSCI0150", "7", "Ratty", "335"));

        } else if (c.equals("CSCI0220")) {
          groups.add(Arrays.asList("1", "Studying for Midterm", "CSCI0220", "5", "CIT", "142"));
          groups.add(Arrays.asList("2", "Drawing Logic Circuits", "CSCI0220", "4",
              "Science Library", "43"));
        } else if (c.equals("CSCI0320")) {
          groups
              .add(Arrays.asList("3", "Talking about Appliances", "CSCI0320", "37", "CIT", "252"));
        }
      }

      Map<String, Object> variables = ImmutableMap.of("groups", groups);
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

      Connection conn = GROUPER_DB.getConnection();
      String query = "INSERT OR IGNORE INTO users VALUES (?, ?, ?);";
      try (PreparedStatement prep = conn.prepareStatement(query)) {
        prep.setString(1, email);
        prep.setString(2, name);
        prep.setInt(3, -1);
        prep.executeUpdate();
        prep.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }

      Map<String, Object> variables = ImmutableMap.of("msg", "success");
      return GSON.toJson(variables);
    }
  }

}
