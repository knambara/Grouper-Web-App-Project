package edu.brown.cs.jkjk.grouper;

/**
 * User class. Represents individual user that logs into the app.
 */

public class User {

  private String name;
  private String email;
  private int userID = -1;
  private int groupID = -1;

  /**
   * Constructor for User.
   * 
   * @param id Unique id of user.
   * @param name Name of user.
   * @param email User's email.
   */
  public User(String name, String email, int groupID) {
    this.name = name;
    this.email = email;
    this.groupID = groupID;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public int getGroupID() {
    return groupID;
  }

  public void setUserID(int id) {
    this.userID = id;
  }

}
