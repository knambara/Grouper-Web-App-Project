package edu.brown.cs.jkjk.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Establishes connection to given database.
 *
 * @author Kento
 *
 */
public class DBConnector {

  private Connection conn = null;
  private String database = null;

  /**
   * Constructor for DBConnector.
   */
  public DBConnector() {
  }

  /**
   * Connects to given database.
   *
   * @param dbFile User entered database file.
   * @throws Exception Throws an Exception when the Database does not exist.
   */
  public void connect(String dbFile) throws Exception {
    if (database != null) {
      if (database.equals(dbFile)) {
        System.out.println("ERROR: Same database as previous one; No change in "
            + "connection");
        return;
      }
    }
    // Close previous connection if new connection is being made.
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        System.out.println("ERROR: " + e.getMessage());
      }
    }
    try {
      // Establish new connection
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + dbFile;
      conn = DriverManager.getConnection(urlToDB);
      Statement stat = conn.createStatement();
      stat.executeUpdate("PRAGMA foreign_keys = ON;");
      stat.close();
      database = dbFile;
      // System.out.println("map set to " + dbFile);
    } catch (Exception e) {
      conn = null;
      database = null;
      throw new Exception("Database does not Exist");
    }
  }

  /**
   * Returns the established connection to conn.
   *
   * @return Connection conn
   */
  public Connection getConnection() {
    return conn;
  }

  /**
   * Closes connection to database.
   */
  public void closeConnection() {
    try {
      if (conn != null) {
        conn.close();
      }
    } catch (SQLException e) {
      System.out.println("ERROR: " + e.getMessage());
    }
  }

  /**
   * Disconnects from the currently connected database.
   */
  public void disconnect() {
    this.conn = null;
    this.database = null;
  }

  /**
   * Returns path to database.
   *
   * @return Returns the path
   */
  public String getDatabase() {
    return this.database;
  }

}
