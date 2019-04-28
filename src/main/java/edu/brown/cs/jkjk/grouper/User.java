package edu.brown.cs.jkjk.grouper;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.LoadingCache;

/**
 * User class. Represents individual user that logs into the app.
 * 
 * @author Kento
 *
 */
public class User {

  private String name;
  private String email;
  private int groupID = -1;
  private boolean mod;

  /**
   * Constructor for User. ID of each user is autoincremented.
   * 
   * @param name Name of user.
   * @param email User's email.
   */
  public User(String name, String email) {
    this.name = name;
    this.email = email;
  }

  /**
   * Returns the name of this user.
   * 
   * @return String name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the email address of this user.
   * 
   * @return String email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns the group ID of the group this user is currently in.
   * 
   * @return int groupID
   */
  public int getGroupID() {
    return groupID;
  }

  /**
   * Sets the group ID of the group this user is joining.
   * 
   * @param id int groupID
   */
  public void setGroupID(int id) {
    this.groupID = id;
  }

  /**
   * Returns whether or not this user is a moderator.
   * 
   * @return boolean mod
   */
  public boolean isMod() {
    return this.mod;
  }

  /**
   * Sets this user's moderator status to true or false.
   * 
   * @param m boolean mod
   */
  public void setMod(boolean m) {
    this.mod = m;
  }

  /**
   * Returns the group object this user is in.
   * 
   * @param groupCache LoadingCache containing Group objects
   * @return Group object
   * @throws ExecutionException
   */
  public Group getGroup(LoadingCache<Integer, Group> groupCache) throws ExecutionException {
    return groupCache.getUnchecked(this.groupID);
  }
}
