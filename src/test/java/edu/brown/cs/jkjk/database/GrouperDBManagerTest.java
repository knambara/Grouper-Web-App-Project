package edu.brown.cs.jkjk.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import edu.brown.cs.jkjk.grouper.GroupCacheHandler;
import edu.brown.cs.jkjk.grouper.UserCacheHandler;

public class GrouperDBManagerTest {

  @Test
  public void testSetUpAndAddUser() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);
    GrouperDBManager manager = new GrouperDBManager(userCache, groupCache, db);
    manager.setUpUsersAndGroupsTable();
    String testHash = "ABCD";
    manager.addNewUser(testHash, "John Doe", "john_doe@domain.com");
    Connection conn = db.getConnection();
    String query = "SELECT * FROM users WHERE U_ID = 'john_doe@domain.com'";
    PreparedStatement prep = conn.prepareStatement(query);
    ResultSet rs = prep.executeQuery();
    ArrayList<String> fields = new ArrayList<String>();

    fields.add(rs.getString(1));
    fields.add(rs.getString(2));
    fields.add(rs.getString(3));
    fields.add(rs.getString(4));

    rs.close();
    prep.close();

    assertEquals(fields.get(0), "john_doe@domain.com");
    assertEquals(fields.get(1), "John Doe");
    assertEquals(fields.get(3), testHash);
    db.disconnect();
  }

  @Test
  public void testAddAndRemoveGroup() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);
    GrouperDBManager manager = new GrouperDBManager(userCache, groupCache, db);

    // Add group
    Map<String, String> variables = new HashMap<String, String>();
    variables.put("department", "Computer Science");
    variables.put("building", "CIT");
    variables.put("code", "CSCI0320");
    variables.put("title", "Talking About Appliances");
    variables.put("duration", "4");
    variables.put("room", "3rd floor atrium");
    variables.put("details", "Come talk with us!");
    String modID = "john_doe@domain.com";
    manager.addNewGroup(variables, modID);

    // Extract newly added group data
    Connection conn = db.getConnection();
    String query = "SELECT * FROM groups WHERE title = 'Talking About Appliances'";
    PreparedStatement prep = conn.prepareStatement(query);
    ResultSet rs = prep.executeQuery();
    ArrayList<String> testGroup = new ArrayList<String>();

    testGroup.add(rs.getString(2));
    testGroup.add(rs.getString(3));
    testGroup.add(rs.getString(4));
    testGroup.add(rs.getString(5));
    testGroup.add(rs.getString(7));
    testGroup.add(rs.getString(8));
    testGroup.add(rs.getString(10));

    prep.close();
    rs.close();

    // Confirm data is as expected
    assertEquals(testGroup.get(0), "CSCI0320");
    assertEquals(testGroup.get(1), "Computer Science");
    assertEquals(testGroup.get(2), "Talking About Appliances");
    assertEquals(testGroup.get(3), "4.0");
    assertEquals(testGroup.get(4), "john_doe@domain.com");
    assertEquals(testGroup.get(5), "CIT");
    assertEquals(testGroup.get(6), "3rd floor atrium");

    // Remove group
    manager.removeGroup(modID);
    db.disconnect();
  }

  @Test
  public void testCheckExpiredGroups() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);
    GrouperDBManager manager = new GrouperDBManager(userCache, groupCache, db);

    assertTrue(manager.checkExpiredGroup());
    db.disconnect();
  }

  @Test
  public void testAddAndRemoveUserFromGroup() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);
    GrouperDBManager manager = new GrouperDBManager(userCache, groupCache, db);

    manager.addUserToGroup("no_one@brown.edu", 2);

    // Test add was successful
    Connection conn = db.getConnection();
    String query = "SELECT G_ID FROM users WHERE U_ID = 'no_one@brown.edu'";
    PreparedStatement prep = conn.prepareStatement(query);
    ResultSet rs = prep.executeQuery();

    assertTrue(rs.getInt(1) == 2);

    rs.close();
    prep.close();

    manager.removeUserFromGroup("no_one@brown.edu", 2);

    // Test remove was successful
    String query2 = "SELECT G_ID FROM users WHERE U_ID = 'no_one@brown.edu'";
    PreparedStatement prep2 = conn.prepareStatement(query2);
    ResultSet rs2 = prep2.executeQuery();

    assertTrue(rs2.getInt(1) == -1);

    rs2.close();
    prep2.close();
    db.disconnect();
  }

  @Test
  public void testGettingDepartmentCourse() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);
    GrouperDBManager manager = new GrouperDBManager(userCache, groupCache, db);

    List<String> res1 = manager.getDepartmentCourses("Computer Science");
    assertEquals(res1.size(), 2);
    assertTrue(res1.contains("CSCI0320"));
    assertTrue(res1.contains("CSCI0160"));

    List<String> res2 = manager.getDepartmentCourses("Applied Math");
    assertEquals(res2.size(), 1);
    assertTrue(res2.contains("APMA0330"));
    db.disconnect();
  }

  @Test
  public void testUserIDFromHash() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);
    GrouperDBManager manager = new GrouperDBManager(userCache, groupCache, db);

    assertEquals(manager.getUserIDFromHash("ABCD"), "blueno@brown.edu");
    assertEquals(manager.getUserIDFromHash("42WW"), "p_sherman@brown.edu");
    assertEquals(manager.getUserIDFromHash("ASDF"), "jane_smith@brown.edu");
    assertEquals(manager.getUserIDFromHash("BEEZ"), "aunt_jemima@brown.edu");
    assertEquals(manager.getUserIDFromHash("ARYA"), "no_one@brown.edu");
    db.disconnect();
  }

  @Test
  public void testIsUserMod() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);
    GrouperDBManager manager = new GrouperDBManager(userCache, groupCache, db);

    assertTrue(manager.isUserMod("ABCD", 1));
    assertTrue(manager.isUserMod("42WW", 3));
    assertTrue(manager.isUserMod("ASDF", 2));
    assertFalse(manager.isUserMod("BEEZ", 1));
    assertFalse(manager.isUserMod("ARYA", 3));
    db.disconnect();
  }

  @Test
  public void testUserExistence() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);
    GrouperDBManager manager = new GrouperDBManager(userCache, groupCache, db);

    assertTrue(manager.doesUserExist("ABCD"));
    assertFalse(manager.doesUserExist("1234"));
    db.disconnect();
  }

  private static String generateHash(int length) {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder hash = new StringBuilder();
    Random random = new Random();
    while (hash.length() < length) {
      int index = (int) (random.nextFloat() * chars.length());
      hash.append(chars.charAt(index));
    }
    String outStr = hash.toString();
    return outStr;
  }
}
