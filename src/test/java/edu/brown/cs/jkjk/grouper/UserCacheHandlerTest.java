package edu.brown.cs.jkjk.grouper;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.brown.cs.jkjk.database.DBConnector;

public class UserCacheHandlerTest {

  @Test
  public void testGetUser() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);

    User user1 = userCache.getUser("blueno@brown.edu");
    assertTrue(user1.getName().equals("Blueno"));
  }

  @Test
  public void testNonexistantUser() throws Exception {
    DBConnector db = new DBConnector();
    db.connect("data/testGrouperDB2.sqlite3");
    UserCacheHandler userCache = new UserCacheHandler(db);

    User user1 = userCache.getUser("not_here@brown.edu");
    assertTrue(user1 == null);
  }
}
