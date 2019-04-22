package edu.brown.cs.jkjk.grouper;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

//kvlynch -- added start time as a field stored within the group
// added the ability to set/get a moderator
public class Group {

  private static int CURR_GROUP_ID = 0;

  private Set<User> users = new HashSet<>();
  private int groupID;
  private String department;
  private String courseCode;
  private String location;
  private String description;
  private double duration;
  private Timestamp start;
  private Integer moderator;
  private String room;
  private Boolean visible;
  private String details;

  public Group(String department, String location, String courseCode,
               String description, double duration, String room, String details) {
    this.department = department;
    this.location = location;
    this.courseCode = courseCode;
    this.description = description;
    this.duration = duration;
    this.start = null;
    this.room = room;
    visible = true;
    this.details = details;
    this.groupID = CURR_GROUP_ID;
    CURR_GROUP_ID += 1;
  }

  public Group(Integer groupID, String department, String location,
               String courseCode, String description, double duration,
               String room, String details) {
    this.department = department;
    this.location = location;
    this.courseCode = courseCode;
    this.description = description;
    this.duration = duration;
    this.start = null;
    this.room = room;
    visible = true;
    this.details = details;
    this.groupID = groupID;
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

  public Timestamp getStartTime() { return start; }

  public void setStartTime(Timestamp start) { this.start = start; }

  public void setModerator(Integer moderator) {this.moderator = moderator; }

  public Integer getModerator() {return moderator; }

  public void setVisible() { visible = true; }

  public void setInvisible() { visible = false; }

  public Boolean getVisibility() { return visible; }

  public String getRoom() { return room; }

  public void addUser(User u) {
    this.users.add(u);
  }

  public void removeUser(User u) {
    this.users.remove(u);
  }

  public String getDetails() { return details; }

}
