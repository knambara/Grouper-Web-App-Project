package edu.brown.cs.jkjk.grouper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Test;

public class GroupAndUserTest {

  @Test
  public void testAddAndRemoveUsers() {
    Group test = new Group(1, "Computer Science", "CIT", "CSCI0320", "Talking About Appliances",
        4.0, "201", "Come talk with us!", new Timestamp(System.currentTimeMillis()));
    User user1 = new User("Blueno", "blueno@brown.edu");
    User user2 = new User("Rock Tree", "rock_tree@brown.edu");
    User user3 = new User("Rhodey", "rhodey@brown.edu");
    test.addUser(user1);
    test.addUser(user2);
    test.addUser(user3);
    List<User> res = test.getUsers();
    assertEquals(res.size(), 3);
    assertTrue(res.get(0).getName().equals("Blueno"));
    assertTrue(res.get(1).getName().equals("Rock Tree"));
    assertTrue(res.get(2).getName().equals("Rhodey"));
    test.removeUser(user1);
    assertEquals(res.size(), 2);
    test.removeUser(user2);
    assertEquals(res.size(), 1);
    test.removeUser(user3);
    assertEquals(res.size(), 0);
  }

  @Test
  public void testDuplicateRemove() {
    Group test = new Group(1, "Computer Science", "CIT", "CSCI0320", "Talking About Appliances",
        4.0, "201", "Come talk with us!", new Timestamp(System.currentTimeMillis()));
    User user1 = new User("Blueno", "blueno@brown.edu");
    test.addUser(user1);
    List<User> res = test.getUsers();
    assertEquals(res.size(), 1);
    test.removeUser(user1);
    assertEquals(res.size(), 0);
    test.removeUser(user1);
    assertEquals(res.size(), 0);
  }

}
