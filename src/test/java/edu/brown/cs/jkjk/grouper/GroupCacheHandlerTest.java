package edu.brown.cs.jkjk.grouper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.brown.cs.jkjk.database.DBConnector;

public class GroupCacheHandlerTest {

  @Test
  public void testGetGroup() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);

    Group res = groupCache.getGroup(1);
    assertTrue(res.getTitle().equals("Talking About Appliances"));
    assertTrue(res.getCourseCode().equals("CSCI0320"));
    assertTrue(res.getDepartment().equals("Computer Science"));
    assertTrue(res.getBuilding().equals("CIT"));
    assertTrue(res.getModerator().equals("blueno@brown.edu"));
  }

  @Test
  public void testGetGroupNonexistent() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);

    Group res = groupCache.getGroup(0);
    assertTrue(res == null);
  }

  @Test
  public void testGetUsers() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);

    List<User> users = groupCache.getUsers(1);
    assertTrue(users.get(0).getName().equals("Blueno"));
    assertTrue(users.get(1).getName().equals("Aunt Jemima"));
  }

  @Test
  public void testGetUsersNotInGroup() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);

    List<User> users = groupCache.getUsers(-1);
    assertTrue(users.get(0).getName().equals("No One"));
  }

  @Test
  public void testGetUsersOnNonexistentGroup() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);

    List<User> users = groupCache.getUsers(7);
    assertTrue(users.size() == 0);
  }

  @Test
  public void testGetDepartmentGroups() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);
    GroupCacheHandler groupCache = new GroupCacheHandler(db, userCache);

    groupCache.getGroup(1);
    groupCache.getGroup(2);
    groupCache.getGroup(3);

    Set<Group> res1 = groupCache.getDepartmentGroups("Computer Science");
    assertEquals(res1.size(), 2);

    Set<Group> res2 = groupCache.getDepartmentGroups("Applied Math");
    assertEquals(res2.size(), 1);
  }

}
