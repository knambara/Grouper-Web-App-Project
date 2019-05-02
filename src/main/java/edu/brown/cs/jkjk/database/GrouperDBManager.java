package edu.brown.cs.jkjk.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.brown.cs.jkjk.grouper.Group;
import edu.brown.cs.jkjk.grouper.GroupCacheHandler;
import edu.brown.cs.jkjk.grouper.User;
import edu.brown.cs.jkjk.grouper.UserCacheHandler;

/**
 * Class manages all modifications in regards to grouper Database.
 *
 * @author Kento, kvlynch
 *
 */
public class GrouperDBManager {

  UserCacheHandler userCache;
  GroupCacheHandler groupCache;
  DBConnector grouperDB;

  /**
   * Constructor for GrouperDBManager.
   *
   * @param userCache Shared instance of userCache
   * @param groupCache Shared instance of groupCache
   * @param grouperDB Shared instance of DBConnector
   */
  public GrouperDBManager(UserCacheHandler userCache, GroupCacheHandler groupCache,
      DBConnector grouperDB) {
    this.userCache = userCache;
    this.groupCache = groupCache;
    this.grouperDB = grouperDB;
  }

  /**
   * Set up users and groups table in the database if they don't exist.
   */
  public void setUpUsersAndGroupsTable() {
    // @formatter:off
    // Create 'users' table if it doesn't exist already
    Connection conn = grouperDB.getConnection();
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
    String query2 = "CREATE TABLE IF NOT EXISTS "
            + "groups(G_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "code TEXT, "
            + "department TEXT, "
            + "description TEXT, "
            + "duration REAL, "
            + "end_time TIMESTAMP, "
            + "Mod TEXT, "
            + "location TEXT, "
            + "visible INTEGER DEFAULT 1, "
            + "room TEXT, "
            + "details TEXT, "
            + "FOREIGN KEY (Mod) REFERENCES users(U_ID) ON DELETE CASCADE ON UPDATE CASCADE);";
    try (PreparedStatement prep2 = conn.prepareStatement(query2)) {
      prep2.executeUpdate();
      prep2.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    // @formatter:on
  }

  /**
   * Adds new user's information into database
   *
   * @param hash Newly generated hash unique for each user.
   * @param username String username
   * @param email String email
   */
  public void addNewUser(String hash, String username, String email) {
    Connection conn = grouperDB.getConnection();

    String query1 = "UPDATE users SET hash=? WHERE U_ID=?;";
    String query2 = "INSERT INTO users(U_ID, name, G_ID, hash) SELECT ?, ?, ?, ? WHERE "
        + "(Select Changes() = 0);";

    try (PreparedStatement prep = conn.prepareStatement(query1)) {
      prep.setString(1, hash);
      prep.setString(2, email);

      prep.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }

    try (PreparedStatement prep = conn.prepareStatement(query2)) {
      prep.setString(1, email);
      prep.setString(2, username);
      prep.setInt(3, -1);
      prep.setString(4, hash);

      prep.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }

    // Assert that user is properly stored in the cache
    User u = userCache.getUser(email);
    assert u.getEmail().equals(email);
  }

  private Timestamp getEndTime(Double duration) {
    Long durMins = Math.round(duration * 60);
    Date endDate = new Date(System.currentTimeMillis() + durMins * 60 * 1000);
    Long endTime = endDate.getTime();
    Timestamp endTS = new Timestamp(endTime);

    return endTS;
  }

  public Integer timeRemaining(Timestamp endTime) {
    Date nowDate = new Date();
    Long time = nowDate.getTime();
    Timestamp nowTime = new Timestamp(time);

    long diff = endTime.getTime() - nowTime.getTime();

    long hours = TimeUnit.MILLISECONDS.toHours(diff);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

    long[] timeLeft = new long[2];
    timeLeft[0] = hours;
    timeLeft[1] = minutes;

    Long trLong = timeLeft[0] * 60 + timeLeft[1];
    Integer tr = trLong.intValue();

    return tr;
  }

  /**
   * Adds new group into database.
   *
   * @param variables
   * @param modID
   *
   * @author kvlynch
   */
  public void addNewGroup(Map<String, String> variables, String modID) {

    String department = variables.get("department");
    String location = variables.get("location");
    String code = variables.get("code");
    String description = variables.get("description");
    Double duration = Double.parseDouble(variables.get("duration"));
    String room = variables.get("room");
    String details = variables.get("details");
    Timestamp end_time = getEndTime(duration);

    Connection conn = grouperDB.getConnection();
    String insert = "INSERT INTO groups (code, department, description, "
        + "duration, end_time, Mod, location, room, details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    try (PreparedStatement prep = conn.prepareStatement(insert)) {
      prep.setString(1, code);
      prep.setString(2, department);
      prep.setString(3, description);
      prep.setDouble(4, duration);
      prep.setTimestamp(5, end_time);
      prep.setString(6, modID);
      prep.setString(7, location);
      prep.setString(8, room);
      prep.setString(9, details);
      prep.executeUpdate();
    } catch (Exception e) {
      System.out.println("ERROR: Could not add group to db.");
      e.printStackTrace();
    }

    // Update user's group in database
    String query = "SELECT G_ID FROM groups WHERE Mod = ?;";
    User mod = userCache.getUser(modID);
    Integer thisGroupID = null;

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, modID);
      try (ResultSet res = prep.executeQuery()) {
        thisGroupID = res.getInt(1);
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    updateUserGroupID(mod, thisGroupID);
    mod.setMod(true);

    // Assert that group is properly stored in the cache
    Group g = groupCache.getGroup(thisGroupID);
    assert g.getGroupID() == thisGroupID;
  }

  /**
   * Get the data and create the groups that exist in the database.
   *
   * @param department A string that represents the selected dept
   * @return a list of groups that exist in that department
   *
   * @author kvlynch
   */
  public List<String> getDepartmentCourses(String department) {
    Connection conn = grouperDB.getConnection();
    List<String> deptGroups = new ArrayList<>();

    String query = "SELECT * FROM groups WHERE department = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, department);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        if (!deptGroups.contains(rs.getString("code"))) {
          deptGroups.add(rs.getString("code"));
        }
      }
    } catch (Exception e) {
      System.out.println("ERROR: Could not get group ids for given dpt.");
    }

    return deptGroups;
  }

  /**
   * Removes group from the database and updates related user's g_id.
   *
   * @param modID
   */
  public void removeGroup(String modID) {
    User mod = userCache.getUser(modID);
    int groupID = mod.getGroupID();
    mod.setMod(false);

    // Update users table for each user that was in mod's group
    List<User> users = groupCache.getUsers(groupID);
    Iterator<User> usersIt = users.iterator();
    while (usersIt.hasNext()) {
      updateUserGroupID(usersIt.next(), -1);
    }

    // Update groups table; delete the group
    Connection conn = grouperDB.getConnection();
    String query = "DELETE FROM groups WHERE Mod = ?";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, modID);
      prep.executeUpdate();
      prep.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not delete the group.");
    }

    // Remove group from cache
    groupCache.getCache().invalidate(groupID);
  }

  /**
   * Handles database and cache when adding user to a group.
   *
   * @param userID String userID
   * @param groupID int groupID
   */
  public void addUserToGroup(String userID, int groupID) {
    // Get references to both user and group objects
    Group g = groupCache.getGroup(groupID);
    User u = userCache.getUser(userID);
    // Add user into specified group
    g.addUser(u);
    // Change group id for user in cache and db
    updateUserGroupID(u, groupID);
  }

  /**
   * Handles database and cache when adding user to a group.
   *
   * @param userID String userID
   * @param groupID int groupID
   */
  public void removeUserFromGroup(String userID, int groupID) {
    // Get references to both user and group objects
    Group g = groupCache.getGroup(groupID);
    User u = userCache.getUser(userID);
    // Remove user from specified group
    g.removeUser(u);
    // Change group id for user back to -1
    updateUserGroupID(u, -1);
  }

  /**
   * Invoked when server closes; all groups should be removed.
   */
  public void deleteAllGroups() {
    Connection conn = grouperDB.getConnection();
    String query = "DROP TABLE IF EXISTS groups;";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    // Change all users' G_ID value back to -1
    String query2 = "UPDATE users SET G_ID = -1";
    try (PreparedStatement prep = conn.prepareStatement(query2)) {
      prep.executeUpdate();
      prep.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not update Users' G_ID.");
    }
  }

  /**
   * Delete groups when the end time is before the current time.
   *
   * Called whenever an user selects a department to search within
   *
   * @author kvlynch
   */
  public void deleteExpiredGroups() {
    Connection conn = grouperDB.getConnection();

    String query = "SELECT G_ID FROM groups WHERE end_time < CURRENT_TIMESTAMP ";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      ResultSet rs = prep.executeQuery();

      while (rs.next()) {
        Integer gId = rs.getInt("G_ID");
        String mId = getModeratorID(gId);
        removeGroup(mId);
      }
      prep.close();
      rs.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not delete expired groups.");
    }
  }

  public Boolean checkExpiredGroup() {
    Connection conn = grouperDB.getConnection();
    Boolean expired = false;

    String query = "SELECT G_ID FROM groups WHERE end_time < CURRENT_TIMESTAMP";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        expired = true;
      }
    } catch (Exception e) {
      System.out.println("ERROR: Problem checking for expired groups.");
    }

    return expired;
  }

  /**
   * Uses a moderator id to find the group id. To be used when a moderator is changing attributes of
   * the group.
   *
   * @param groupId moderator id
   * @return the group id of which they are the moderator
   *
   * @author kvlynch
   */
  private String getModeratorID(Integer groupId) {
    Connection conn = grouperDB.getConnection();
    String mId = null;

    String query = "SELECT Mod FROM groups WHERE G_ID = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, groupId);
      ResultSet rs = prep.executeQuery();
      if (rs.next()) {
        mId = rs.getString("Mod");
      }
      prep.close();
      rs.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not get Mod ID from  G_ID");
    }
    return mId;
  }

  /**
   * Returns the userID that matches given hash.
   *
   * @param hash
   * @return String userID
   */
  public String getUserIDFromHash(String hash) {
    Connection conn = grouperDB.getConnection();
    String query = "SELECT U_ID FROM users WHERE hash = ?;";
    String userID = null;
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, hash);
      try (ResultSet res = prep.executeQuery()) {
        userID = res.getString(1);
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return userID;
  }

  /**
   * Updates specified user's G_ID info in cache and database.
   *
   * @param u User u
   * @param newGID Integer groupID
   */
  private void updateUserGroupID(User u, int newGID) {
    // Update user objects's field
    u.setGroupID(newGID);
    // Prepare query to db
    Connection conn = grouperDB.getConnection();
    String query = "UPDATE users SET G_ID = ? WHERE U_ID = ?";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, newGID);
      prep.setString(2, u.getEmail());
      prep.executeUpdate();
      prep.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not update Users' G_ID.");
    }
  }

}
