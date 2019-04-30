package edu.brown.cs.jkjk.grouper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles the group logic.
 *
 * @author kvlynch
 */
public class GroupControl {

  private GroupCacheHandler groupCache;
  private UserCacheHandler userCache;

  /**
   * Constructor for GroupControl.
   *
   * @param database DBConnector database
   */
  public GroupControl(UserCacheHandler userCache, GroupCacheHandler groupCache) {
    this.groupCache = groupCache;
    this.userCache = userCache;
  }

  /**
   * To be used on the initial filter page, once the academic department has been entered by the
   * user. Returns all course codes with active and visible groups.
   *
   * @param department an academic department
   * @return the set of courses with existing groups
   */
  public Set<String> getDepartmentCourses(String department) {
    Set<String> courseList = new HashSet<>();
    Iterator<Group> deptGroups = groupCache.getDepartmentGroups(department).iterator();
    while (deptGroups.hasNext()) {
      Group g = deptGroups.next();
      String course = g.getCourseCode();
      if (g.getVisibility()) {
        courseList.add(course);
      }
    }
    return courseList;
  }

  /**
   * To be used on the filter page, once the user has selected the courses to search within.
   * 
   * @param courses Set of strings which are course codes
   * @return the set of visible groups for the given courses
   */
  public Set<Group> getCourseGroups(String department, Set<String> courses) {
    Set<Group> groups = new HashSet<>();
    Iterator<Group> deptGroups = groupCache.getDepartmentGroups(department).iterator();
    while (deptGroups.hasNext()) {
      Group g = deptGroups.next();
      if (courses.contains(g.getCourseCode())) {
        groups.add(g);
      }
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

      // todo: find the time remaining
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
   * Returns parameters necessary for group pages.
   * 
   * @param Current userID
   * @return HashMap of group page information.
   */
  public Map<String, Object> getGroupView(String userId) {
    Integer groupId = userCache.getUser(userId).getGroupID();
    Map<String, Object> info = new HashMap<>();

    // check if the user is the moderator, if so they also get visibility status
    // String modId = getModeratorID(groupId);
    Group g = groupCache.getGroup(groupId);
    List<User> users = g.getUsers();

    String course = g.getCourseCode();
    String description = g.getDescription();
    // Integer userCount = users.size();
    // String location = g.getLocation();
    // String room = g.getRoom();
    String details = g.getDetails();

    info.put("grouptitle", description);
    info.put("groupclass", course);
    info.put("groupusers", users);
    info.put("groupdesc", details);

    return info;
  }

}
