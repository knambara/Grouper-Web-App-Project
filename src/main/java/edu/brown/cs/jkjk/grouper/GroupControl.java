package edu.brown.cs.jkjk.grouper;

import edu.brown.cs.jkjk.database.DBConnector;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Handles the group logic.
 *
 * @author kvlynch
 */
public class GroupControl {

  private DBConnector database;

  /**
   * Constructor for GroupControl.
   *
   * @param database DBConnector database
   */
  public GroupControl(DBConnector database) {
    this.database = database;
  }

  /**
   * To be used on the initial filter page, once the academic department has
   * been entered by the user. Returns all course codes with active and visible
   * groups.
   *
   * @param department an academic department
   * @return the set of courses with existing groups
   */
  public Set<String> getDepartmentCourses(String department) {
    GroupCacheHandler deptCache = new GroupCacheHandler(database);
    Set<String> courseList = new HashSet<>();
    try {
      Iterator<Group> deptGroups = deptCache.getDepartmentGroups(department).iterator();
      while (deptGroups.hasNext()) {
        Group g = deptGroups.next();
        String course = g.getCourseCode();
        if (g.getVisibility()) {
          courseList.add(course);
        }
      }
    } catch (ExecutionException e) {
      System.out.println("ERROR: No courses in the given department.");
    }
    return courseList;
  }

  /**
   * To be used on the filter page, once the user has selected the courses to
   * search within.
   * @param courses Set of strings which are course codes
   * @return the set of visible groups for the given courses
   */
  public Set<Group> getCourseGroups(String department, Set<String> courses) {
    GroupCacheHandler deptCache = new GroupCacheHandler(database);
    Set<Group> groups = new HashSet<>();

    try {
      Iterator<Group> deptGroups = deptCache.getDepartmentGroups(department)
              .iterator();
      while (deptGroups.hasNext()) {
        Group g = deptGroups.next();
        if (courses.contains(g.getCourseCode())) {
          groups.add(g);
        }
      }
    } catch (ExecutionException e) {
      System.out.println("ERROR: " + e.getMessage());
    }
    return groups;
  }

  /**
   * Used to get the information that will be displayed on tiles for the groups
   *
   * @param groups A set of groups
   * @return A map of the group id to the display information
   */
  public Map<Integer, Map<String, Object>> getTileInfo(Set<Group> groups) {
    Map<Integer, Map<String, Object>> info = new HashMap<>();

    Iterator<Group> groupIt = groups.iterator();
    while (groupIt.hasNext()) {
      Group g = groupIt.next();
      Integer gId = g.getGroupID();
      String desc = g.getDescription();
      String course = g.getCourseCode();
      String location = g.getLocation();

      Integer users = g.getUsers().size();

      //todo: find the time remaining
      String timeRemainingPlaceHolder = "HH:MM left";

      Map<String, Object> gInfo = new HashMap<>();
      gInfo.put("description", desc);
      gInfo.put("code", course);
      gInfo.put("location", location);
      gInfo.put("users", users);
      gInfo.put("timeLeft", timeRemainingPlaceHolder);

      info.put(gId, gInfo);
    }
    return info;
  }

  /**
   *
   * @param groupId
   * @return
   */
  public Map<String, Object> getGroupView(Integer groupId) {
    GroupCacheHandler groupCache = new GroupCacheHandler(database);
    Map<String, Object> info = new HashMap<>();

    try {
      Group g = groupCache.getGroup(groupId);

    } catch (Exception e) {
      System.out.println("ERROR: Could not get detailed group info.");
    }

    return info;
  }

  /**
   * Takes in the group variables as a map and sets creates a group to be
   * inserted into the database.
   *
   * @param variables the variables inputted by the user for the group
   */
  public void newGroup(Map<String, String> variables, Integer modId) {
    String department = variables.get("department");
    String location = variables.get("location");
    String code = variables.get("code");
    String description = variables.get("description");
    Double duration = Double.parseDouble(variables.get("duration"));
    String room = variables.get("room");
    String details = variables.get("details");

    //need to make a new group to get the group id
    Group g = new Group(department, location, code, description, duration, room, details);

    Integer gId = g.getGroupID();
    g.setModerator(modId);

    Connection conn = database.getConnection();
    String insert = "INSERT INTO groups (G_ID, code, department, description," +
            "duration, Mod, location, room, details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    try {
      PreparedStatement prep = conn.prepareStatement(insert);
      prep.setInt(1, gId);
      prep.setString(2, code);
      prep.setString(3, department);
      prep.setString(4, description);
      prep.setDouble(5, duration);
      prep.setInt(6, modId);
      prep.setString(7, location);
      prep.setString(8, room);
      prep.setString(9, details);
      prep.addBatch();
      prep.executeBatch();

      prep.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not add group to db.");
    }
  }

  /**
   * Delete the group from database when moderator chooses to end group.
   *
   * @param modId moderator id
   */
  public void deleteGroup(Integer modId) {
    //TODO: Update the users database to reflect they are no longer in a group

    Connection conn = database.getConnection();
    String delete = "DELETE FROM groups WHERE Mod = ?";

    try {
      PreparedStatement prep = conn.prepareStatement(delete);
      prep.setInt(1, modId);
      prep.executeUpdate();
      prep.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not delete the group.");
    }
  }

  /**
   * Update the visibility fo the group to new users looking for a group.
   *
   * @param modId integer moderator id
   * @param visible boolean representing if they want group to be visible
   */
  public void updateVisibility(Integer modId, Boolean visible) {
    int visibility;
    if (visible) {
      visibility = 1;
    } else {
      visibility = 0;
    }

    Connection conn = database.getConnection();
    String update = "UPDATE groups SET visible = ? WHERE Mod = ?";

    try {
      PreparedStatement prep = conn.prepareStatement(update);
      prep.setInt(1, visibility);
      prep.setInt(2, modId);
      prep.executeUpdate();
      prep.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not update visibility.");
    }
  }



  /**
   * Uses a moderator id to find the group id. To be used when a moderator is
   * changing attributes of the group.
   *
   * @param modId moderator id
   * @return the group id of which they are the moderator
   */
  private Integer getModeratorGroupID(Integer modId) {
    Connection conn = database.getConnection();
    Integer gId = null;

    String query = "SELECT G_ID FROM groups WHERE Mod = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, modId);
      ResultSet rs = prep.executeQuery();
      if (rs.next()) {
        gId = rs.getInt("G_ID");
      }
      prep.close();
      rs.close();
    } catch (Exception e) {
      System.out.println("ERROR: Could not get G_ID from Mod ID");
    }
    return gId;
  }

  private Integer getUserGroupID(Integer userId) {
    //TODO: statement to get the group id
    return 0;
  }


//  Date date = new Date();
//  long time = date.getTime();
//  Timestamp now = new Timestamp(time);
//  Date date2 = new Date(System.currentTimeMillis()+5*60*1000);
//  long time2 = date2.getTime();
//  Timestamp then = new Timestamp(time2);
//
//  long diff = then.getTime() - now.getTime();
//
//  long hours = TimeUnit.MILLISECONDS.toHours(diff);
//  long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

  //System.out.println(hours); 0
  //System.out.println(minutes); 5

}
