package edu.brown.cs.jkjk.term_proj;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    Spark.get("/study", new LandingPageHandler(), freeMarker);
    Spark.get("/study/dashboard", new DashboardHandler(), freeMarker);
    Spark.get("/study/newgroup", new NewGroupHandler(), freeMarker);
    Spark.get("/study/group", new GroupHandler(), freeMarker);

    Spark.post("/newuser", new NewUserHandler());

  }

  /**
   * Handler for the landing page of the site.
   *
   * @author jsoenkse
   */
  private static class LandingPageHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Study");
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
      Map<String, Object> variables = ImmutableMap.of("title", "Study - Your dashboard");
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
      Map<String, Object> variables = ImmutableMap.of("title", "Study - Create a new group");
      return new ModelAndView(variables, "newgroup.ftl");
    }
  }

  /**
   * Handler for the page that allows you to view and moniter your current group.
   *
   * @author jsoenkse
   */
  private static class GroupHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Study - Group status", "grouptitle",
          "Group Title", "groupclass", "CLAS1234", "groupdesc", "A group with a description",
          "groupemails", "jeffrey_demanche@brown.edu");
      return new ModelAndView(variables, "group.ftl");
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
