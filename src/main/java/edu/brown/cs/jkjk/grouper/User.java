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
  private String img;
  private double[] position;

  /**
   * Constructor for User. ID of each user is autoincremented.
   * 
   * @param name Name of user.
   * @param email User's email.
   */
  public User(String name, String email, String img) {
    this.name = name;
    this.email = email;
    this.img = img;
    position = new double[2];
    position[0] = 0;
    position[1] = 0;
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
   * Return the URL of the user's profile image.
   *
   * @return img the URL
   */
  public String getImg() {
    return img;
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
   * Sets the position of the user to a specified lat/lon coordinate.
   *
   * @param position array of 2 doubles, where 0-ind = lat and 1-ind = lon
   */
  public void setPosition(double[] position) {
    this.position = position;
  }

  /**
   * Returns the user's position.
   *
   * @return array of 2 doubles, where 0-ind = lat and 1-ind = lon
   */
  public double[] getPosition() {
    return position;
  }

  public double getDistance(double[] building) {
    if (position[0] != 0 && position[1] != 0) {
      double latDiff = position[0] - building[0];
      double latSq = Math.pow(latDiff, 2);
      double lonDiff = position[1] - building[1];
      double lonSq = Math.pow(lonDiff, 2);

      double sum = latSq + lonSq;

      return Math.pow(sum, 0.5);
    } else {
      return 0;
    }
  }

  /**
   * Returns the group object this user is in.
   * 
   * @param groupCache LoadingCache containing Group objects
   * @return Group object
   * @throws ExecutionException if there is an issue
   */
  public Group getGroup(LoadingCache<Integer, Group> groupCache) throws ExecutionException {
    return groupCache.getUnchecked(this.groupID);
  }
}
