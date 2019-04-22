package edu.brown.cs.jkjk.grouper;

import java.util.HashSet;
import java.util.Set;

public class Group {

  private static int CURR_GROUP_ID = 0;

  private Set<User> users = new HashSet<>();
  private int groupID;
  private String department;
  private String courseCode;
  private String location;
  private String description;
  private double duration;

  public Group(String department, String location, String courseCode,
      String description, double duration) {
    this.department = department;
    this.location = location;
    this.courseCode = courseCode;
    this.description = description;
    this.duration = duration;
    this.groupID = CURR_GROUP_ID;
    CURR_GROUP_ID += 1;
  }

  public Set<User> getUsers() {
    return users;
  }

  public int getGroupID() {
    return groupID;
  }

  public String getDepartment() {
    return department;
  }

  public String getLocation() {
    return location;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public String getDescription() {
    return description;
  }

  public double getDuration() {
    return duration;
  }

  public void addUser(User u) {
    this.users.add(u);
  }

  public void removeUser(User u) {
    this.users.remove(u);
  }

}
