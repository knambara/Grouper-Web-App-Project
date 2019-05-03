package edu.brown.cs.jkjk.grouper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.brown.cs.jkjk.database.DBConnector;

/**
 * Handler for fetching Group object with given in; query the db if the group does not exits
 *
 * @author kvlynch
 */
public class GroupCacheHandler {

  private static final int TIME_LIMIT = 24;
  private static final int MAX_SIZE = 1000;

  private DBConnector database;
  private UserCacheHandler userCache;

  // Cache that stores the groups by group id
  private LoadingCache<Integer, Group> groupCache = CacheBuilder.newBuilder()
      .expireAfterWrite(TIME_LIMIT, TimeUnit.HOURS).maximumSize(MAX_SIZE)
      .build(new CacheLoader<Integer, Group>() {
        @Override
        public Group load(Integer groupID) throws Exception {

          Connection conn = database.getConnection();
          Group g = null;
          // Create and return Group with given id
          String query = "SELECT * FROM groups WHERE G_ID = ?";
          try (PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, groupID);
            try (ResultSet rs = prep.executeQuery()) {
              while (rs.next()) {
                Integer groupId = rs.getInt("G_ID");
                String code = rs.getString("code");
                String department = rs.getString("department");
                String title = rs.getString("title");
                Double duration = rs.getDouble("duration");
                Timestamp end_time = rs.getTimestamp("end_time");
                String moderator = rs.getString("Mod");
                String building = rs.getString("building");
                String room = rs.getString("room");
                String details = rs.getString("details");

                g = new Group(groupId, department, building, code, title, duration, room, details,
                    end_time);
                g.setModerator(moderator);
                // add the users to the group
                List<User> users = getUsers(groupId);
                for (User u : users) {
                  g.addUser(u);
                }
              }
            } catch (SQLException e) {
              System.out.println(e.getMessage());
            }
          } catch (SQLException e) {
            System.out.println(e.getMessage());
          }
          return g;
        }
      });

  /**
   * Constructor for the GroupCacheHandler
   *
   * @param database DBConnector database
   * @param userCache UserCacheHandler instance
   */
  public GroupCacheHandler(DBConnector database, UserCacheHandler userCache) {
    this.database = database;
    this.userCache = userCache;
  }

  /**
   * Returns the group guava cache object.
   * 
   * @return LoadingCache of Group
   */
  public LoadingCache<Integer, Group> getCache() {
    return groupCache;
  }

  /**
   * Returns specified Group object from cache.
   * 
   * @param id int group id
   * @return Group group
   */
  public Group getGroup(int id) {
    return groupCache.getUnchecked(id);
  }

  /**
   * Returns set of User with specified group ID
   * 
   * @param groupID String id of the group
   * @return list of Users
   */
  public List<User> getUsers(Integer groupID) {

    Connection conn = database.getConnection();
    List<User> users = new ArrayList<>();

    String query = "SELECT U_ID FROM users WHERE G_ID = ?";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, groupID);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          User u = userCache.getUser(rs.getString(1));
          users.add(u);
        }
      } catch (SQLException e) {

      }
    } catch (Exception e) {
      System.out.println("ERROR: Could not get users from G_ID");
    }

    return users;
  }

  /**
   * Return groups studying subject in specified department
   * 
   * @param dept String department name
   * @return Set of Groups
   */
  public Set<Group> getDepartmentGroups(String dept) {
    Set<Group> deptGroups = new HashSet<>();
    for (Group g : groupCache.asMap().values()) {
      if (g.getDepartment().equals(dept)) {
        deptGroups.add(g);
      }
    }
    return deptGroups;
  }
}
