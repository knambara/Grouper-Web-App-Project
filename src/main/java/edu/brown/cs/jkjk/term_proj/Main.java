package edu.brown.cs.jkjk.term_proj;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * This is the Main class of our term project.
 *
 * @author jsoenkse, jdemanch, knambara, kvlynch
 */
public abstract class Main {

  private static final int PORT_NUM = 4567;

  /**
   * Method entrypoint for CLI invocation.
   *
   * @param args Arguments passed on the command line.
   * @throws SQLException
   */
  public static void main(String[] args) {
    OptionParser parser = new OptionParser();

    parser.accepts("gui");

    OptionSpec<Integer> portSpec = parser.accepts("port").withRequiredArg()
        .ofType(Integer.class);

    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      if (options.has(portSpec)) {
        Spark.port(options.valueOf(portSpec));
      } else {
        Spark.port(PORT_NUM);
      }

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
  }

  // Below here, there isn't much for a new CS32 student to worry
  // about understanding.

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.\n",
          templates);
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
}
