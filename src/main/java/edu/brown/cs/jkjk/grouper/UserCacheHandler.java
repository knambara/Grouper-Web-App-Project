package edu.brown.cs.jkjk.grouper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
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
  private LoadingCache<Integer, User> userCache = CacheBuilder.newBuilder()
      .expireAfterWrite(TIME_LIMIT, TimeUnit.HOURS).maximumSize(MAX_SIZE)
      .build(new CacheLoader<Integer, User>() {
        @Override
        public User load(Integer userID) throws Exception {

          Connection conn = database.getConnection();
          User u = null;

          // Create and return User with given id
          String query = "SELECT * FROM users WHERE P_ID = ?";
          try (PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, userID);
            try (ResultSet res = prep.executeQuery()) {
              if (res.next()) {
                String name = res.getString(2);
                String email = res.getString(3);
                int groupID = res.getInt(4);
                u = new User(name, email);
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
   * @param id int id
   * @return User user
   * @throws ExecutionException Upon connection error to the database
   */
  public User getUser(int id) throws ExecutionException {
    return userCache.get(id);
  }
}
