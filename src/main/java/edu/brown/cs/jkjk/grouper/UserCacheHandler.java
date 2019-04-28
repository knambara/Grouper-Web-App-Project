package edu.brown.cs.jkjk.grouper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.brown.cs.jkjk.database.DBConnector;

/**
 * Handler for fetching User object with given id; query db if it doesnn't exist.
 * 
 * @author Kento
 *
 */
public class UserCacheHandler {

  private static final int TIME_LIMIT = 24;
  private static final int MAX_SIZE = 1000;

  private DBConnector database;
  private LoadingCache<String, User> userCache = CacheBuilder.newBuilder()
      .expireAfterWrite(TIME_LIMIT, TimeUnit.HOURS).maximumSize(MAX_SIZE)
      .build(new CacheLoader<String, User>() {
        @Override
        public User load(String email) throws Exception {

          Connection conn = database.getConnection();
          User u = null;

          // Create and return User with given id
          String query = "SELECT * FROM users WHERE U_ID = ?";
          try (PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setString(1, email);
            try (ResultSet res = prep.executeQuery()) {
              if (res.next()) {
                String user_email = res.getString(1);
                String user_name = res.getString(2);
                int groupID = res.getInt(3);
                u = new User(user_name, user_email);
                u.setGroupID(groupID);
              }
            } catch (SQLException e) {
              System.out.println("ERROR: " + e.getMessage());
            }
          } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
          }
          return u;
        }
      });

  /**
   * Constructor for UserCacheHandler.
   * 
   * @param database DBConnector database
   */
  public UserCacheHandler(DBConnector database) {
    this.database = database;
  }

  /**
   * Returns specified User object from cache.
   * 
   * @param id String id
   * @return User user
   */
  public User getUser(String id) {
    return userCache.getUnchecked(id);
  }
}
