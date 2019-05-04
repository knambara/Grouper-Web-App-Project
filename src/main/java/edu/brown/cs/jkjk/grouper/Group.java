package edu.brown.cs.jkjk.grouper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that models a Group.
 *
 * @author kvlynch, knambara
 */
public class Group {

  private List<User> users = new ArrayList<>();
  private int groupID;
  private String department;
  private String courseCode;
  private String building;
  private String title;
  private double duration;
  private Timestamp end;
  private String moderator;
  private String room;
  private Boolean visible;
  private String details;

  public Group(Integer groupID, String department, String building, String courseCode, String title,
      double duration, String room, String details, Timestamp end) {
    this.department = department;
    this.building = building;
    this.courseCode = courseCode;
    this.title = title;
    this.duration = duration;
    this.end = end;
    this.room = room;
    visible = true;
    this.details = details;
    this.groupID = groupID;
  }

  public List<User> getUsers() {
    return users;
  }

  public int getGroupID() {
    return groupID;
  }

  public String getDepartment() {
    return department;
  }

  public String getBuilding() {
    return building;
  }

  public String getCourseCode() {
    return courseCode;
  }

  public String getTitle() {
    return title;
  }

  public double getDuration() {
    return duration;
  }

  public void setDuration(double duration) {
    this.duration = duration;
  }

  public Timestamp getEndTime() {
    return end;
  }

  public void setEndTime(Timestamp end) {
    this.end = end;
  }

  public void setModerator(String moderator) {
    this.moderator = moderator;
  }

  public String getModerator() {
    return moderator;
  }

  public void setVisible() {
    visible = true;
  }

  public void setInvisible() {
    visible = false;
  }

  public Boolean getVisibility() {
    return visible;
  }

  public String getRoom() {
    return room;
  }

  public void addUser(User u) {
    this.users.add(u);
  }

  public void removeUser(User u) {
    this.users.remove(u);
  }

  public String getDetails() {
    return details;
  }

}
