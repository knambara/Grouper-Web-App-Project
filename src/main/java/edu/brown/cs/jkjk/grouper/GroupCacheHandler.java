package edu.brown.cs.jkjk.grouper;

import java.sql.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.brown.cs.jkjk.database.DBConnector;

/**
 * Handler for fetching Group object with given in; query the db if the group
 * does not exits
 *
 * @author kvlynch
 */
public class GroupCacheHandler {

  private static final int TIME_LIMIT = 24;
  private static final int MAX_SIZE = 1000;

  private DBConnector database;

  //Cache that stores the groups by group id
  private LoadingCache<Integer, Group> groupCache = CacheBuilder.newBuilder()
          .expireAfterWrite(TIME_LIMIT, TimeUnit.HOURS).maximumSize(MAX_SIZE)
          .build(new CacheLoader<Integer, Group>() {
            @Override
            public Group load(Integer groupID) throws Exception {

              Connection conn = database.getConnection();
              Group g = null;

              //Create and return Group with given id
              String query = "SELECT * FROM groups WHERE G_ID = ?";
              try {
                PreparedStatement prep = conn.prepareStatement(query);
                prep.setInt(1, groupID);
                ResultSet rs = prep.executeQuery();
                if (rs.next()) {
                  g = groupHelper(rs).iterator().next();
                }
                prep.close();
                rs.close();
              } catch (SQLException e) {
                System.out.println("ERROR: " + e.getMessage());
              }
              return g;
            }
          });

  //Cache that stores the groups by department id
  private LoadingCache<String, Set<Group>> departmentCache = CacheBuilder.newBuilder()
          .expireAfterWrite(TIME_LIMIT, TimeUnit.HOURS).maximumSize(MAX_SIZE)
          .build(new CacheLoader<String, Set<Group>>() {
            @Override
            public Set<Group> load(String department) throws Exception {
              Connection conn = database.getConnection();
              Set<Group> groups = null;

              //Find all groups in the given department
              String query = "SELECT * FROM groups WHERE department = ?";
              try {
                PreparedStatement prep = conn.prepareStatement(query);
                prep.setString(1, department);
                ResultSet rs = prep.executeQuery();
                if (rs.next()) {
                  groups = groupHelper(rs);
                }
                prep.close();
                rs.close();
              } catch (SQLException e) {
                System.out.println("ERROR: " + e.getMessage());
              }
              return groups;
            }
          });

  private Set<Group> groupHelper(ResultSet rs) {


    Set<Group> groups = new HashSet<>();
    try {
      while (rs.next()) {
        Integer groupId = rs.getInt("G_ID");
        String code = rs.getString("code");
        String department = rs.getString("department");
        String description = rs.getString("description");
        Double duration = rs.getDouble("duration");
        Timestamp start = rs.getTimestamp("start");
        String moderator = rs.getString("Mod");
        String location = rs.getString("location");
        String room = rs.getString("room");
        String details = rs.getString("details");

        Group g = new Group(groupId, department, location, code, description, duration, room, details);
        g.setModerator(moderator);
        g.setStartTime(start);

        //add the users to the group
        Set<User> users = getUsers(groupId);
        Iterator<User> usersIt = users.iterator();
        while (usersIt.hasNext()) {
          g.addUser(usersIt.next());
        }

        groups.add(g);
      }

    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
    }
    return groups;
  }

  /**
   * Constructor for the GroupCacheHandler
   *
   * @param database DBConnector database
   */
  public GroupCacheHandler(DBConnector database) { this.database = database; }

  /**
   * Returns specified Group object from cache.
   * @param id int group id
   * @return Group group
   * @throws ExecutionException Upon connection error to the database
   */
  public Group getGroup(int id) throws ExecutionException {
    return groupCache.get(id);
  }

  public Set<Group> getDepartmentGroups(String department) throws ExecutionException {
    return departmentCache.get(department);
  }

  private Set<User> getUsers(Integer groupId) {
    UserCacheHandler userCache = new UserCacheHandler(database);

    Connection conn = database.getConnection();
    Set<User> users = new HashSet<>();

    String query = "SELECT U_ID FROM users WHERE G_ID = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, groupId);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        //User u = userCache.getUser(rs.getString(1));
        //users.add(u);
      }
      rs.close();
      prep.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not get users from G_ID");
    }

    return users;
  }


}
