package edu.brown.cs.jsoenkse.bacon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.brown.cs.jsoenkse.weightedGraphs.Dijkstra;

/**
 * This BaconVertexEdgeTest tests the functionality of the BaconVertex and
 * BaconEdge classes, as well as the path returned from running Dijkstras on two
 * BaconVertices.
 *
 * @author jennasoenksen
 */
public class BaconVertexEdgeTest {

  private Connection conn;

  /**
   * Quick test to confirm that the BaconEdge opposite function works as
   * intended.
   */
  @Test
  public void testEdgeOppositeFunction() {
    this.setConnection("data/bacon/smallBacon.sqlite3");
    BaconVertex v1 = new BaconVertex("Taylor Swift");
    BaconVertex v2 = new BaconVertex("Shirley McLaine");
    BaconEdge edge = new BaconEdge(v1, v2, 0.5, "Valentine's Day");
    assertTrue(edge.opposite(v1).getName().equals("Shirley McLaine"));
    assertTrue(edge.opposite(v2).getName().equals("Taylor Swift"));
  }

  /**
   * Quick test to make sure that the BaconEdge sameConnection function works in
   * determining if two edges connect the same actors.
   */
  @Test
  public void testSameConnectionFunction() {
    BaconVertex v1 = new BaconVertex("That One Actor");
    BaconVertex v2 = new BaconVertex("The Other Actor");
    BaconEdge e1 = new BaconEdge(v1, v2, 0.5, "This Movie");
    BaconEdge e2 = new BaconEdge(v1, v2, 0.8, "That Movie");
    assertTrue(e1.sameConnection(e2));
    BaconVertex v3 = new BaconVertex("That One Actor");
    BaconVertex v4 = new BaconVertex("The Other Actor");
    BaconEdge e3 = new BaconEdge(v3, v4, 1.3, "That Other Movie");
    assertTrue(e2.sameConnection(e3));
    assertTrue(e1.sameConnection(e3));
  }

  /**
   * Test for just finding a single edge from a BaconVertex and confirming its
   * properties.
   */
  @Test
  public void simpleEdgeFinderAndConfirmation() throws SQLException {
    this.setConnection("data/bacon/smallBacon.sqlite3");
    BaconVertex vertex = new BaconVertex("Taylor Swift");
    vertex.setConnection(conn);
    Set<BaconEdge> edges = vertex.getEdges();
    assertEquals(edges.size(), 1);
    BaconEdge edge = null;
    for (BaconEdge e : edges) {
      edge = e;
    }
    assertEquals(edge.getMovie(), "Valentine's Day");
    assertTrue(edge.getWeight() == 0.5);
    assertEquals(edge.opposite(vertex).getName(), "Shirley McLaine");
  }

  /**
   * Test to make sure only valid edges (by the name rule) are added to the edge
   * set.
   */
  @Test
  public void findOnlyValidEdgeFromTwoChoicesTest() {
    this.setConnection("data/bacon/smallBacon.sqlite3");
    BaconVertex vertex = new BaconVertex("Cate Blanchett", conn);
    Set<BaconEdge> edges = vertex.getEdges();
    assertEquals(edges.size(), 1);
    BaconEdge edge = null;
    for (BaconEdge e : edges) {
      edge = e;
    }
    assertEquals(edge.getMovie(), "Ponyo");
    assertTrue(edge.getWeight() == 0.5);
    assertEquals(edge.opposite(vertex).getName(), "Betty White");
  }

  /**
   * Test to make sure getting edges on a vertex with an actor not in the
   * database returns an empty set without crashing anything.
   */
  @Test
  public void findEdgesOnActorNotInDb() {
    this.setConnection("data/bacon/smallBacon.sqlite3");
    BaconVertex vertex = new BaconVertex("Emma Watson", conn);
    Set<BaconEdge> edges = vertex.getEdges();
    assertEquals(edges.size(), 0);
  }

  /**
   * Test to make sure that running Dijkstras between two BaconVertices outputs
   * a path that hits all of the correct actors.
   */
  @Test
  public void testSimpleDijkstrasHitsCorrectVertices() {
    this.setConnection("data/bacon/smallBacon.sqlite3");
    BaconVertex vertex1 = new BaconVertex("Taylor Swift", conn);
    BaconVertex vertex2 = new BaconVertex("Sylvester Stallone", conn);
    Dijkstra dj = new Dijkstra();
    List<BaconVertex> path = dj.shortestPath(vertex1, vertex2);
    assertTrue(path.size() == 6);
    assertTrue(path.get(0).getName().equals("Taylor Swift"));
    assertTrue(path.get(1).getName().equals("Shirley McLaine"));
    assertTrue(path.get(2).getName().equals("Matthew McConaughey"));
    assertTrue(path.get(3).getName().equals("Mini Anden"));
    assertTrue(path.get(4).getName().equals("Amy Stiller"));
    assertTrue(path.get(5).getName().equals("Sylvester Stallone"));
  }

  /**
   * Test to make sure that given the vertex path returned from Dijkstras, you
   * can reconstruct the edges of that path to output the proper movies.
   */
  @Test
  public void testSimpleTracingEdgesBackFromDijkstraOutput() {
    this.setConnection("data/bacon/smallBacon.sqlite3");
    BaconVertex start = new BaconVertex("Taylor Swift", conn);
    BaconVertex end = new BaconVertex("Sylvester Stallone", conn);
    Dijkstra dj = new Dijkstra();
    List<BaconVertex> path = dj.shortestPath(start, end);
    List<BaconEdge> edgePath = new ArrayList<BaconEdge>();
    BaconVertex v1 = start;
    for (int i = 1; i < path.size(); i++) {
      BaconEdge e = v1.connectingEdge(path.get(i));
      edgePath.add(e);
      v1 = path.get(i);
    }
    assertTrue(edgePath.size() == 5);
    assertTrue(edgePath.get(0).getMovie().equals("Valentine's Day"));
    assertTrue(edgePath.get(1).getMovie().equals("Bernie"));
    assertTrue(edgePath.get(2).getMovie().equals("Tropic Thunder"));
    assertTrue(edgePath.get(3).getMovie().equals("Tropic Thunder"));
    assertTrue(edgePath.get(4).getMovie().equals("Lovers and Other Strangers"));
  }

  /**
   * This test makes sure that finding the shortest path between two actors that
   * are not connected is empty.
   */
  @Test
  public void testNoConnectionOnSmallDB() {
    this.setConnection("data/bacon/smallBacon.sqlite3");
    BaconVertex start = new BaconVertex("Fabio Stallone", conn);
    BaconVertex end = new BaconVertex("Taylor Swift", conn);
    Dijkstra dj = new Dijkstra();
    List<BaconVertex> path = dj.shortestPath(start, end);
    assertTrue(path.size() == 0);
  }

  /**
   * Test for a connection on the big bacon database.
   */
  @Test
  public void testDijkstraOnBigDB() {
    this.setConnection("data/bacon/bacon.sqlite3");
    BaconVertex start = new BaconVertex("Sam Worthington", conn);
    BaconVertex end = new BaconVertex("Steve Lemme", conn);
    Dijkstra dj = new Dijkstra();
    List<BaconVertex> path = dj.shortestPath(start, end);
    assertTrue(path.size() == 7);
    assertTrue(path.get(0).getName().equals("Sam Worthington"));
    assertTrue(path.get(1).getName().equals("Wes Studi"));
    assertTrue(path.get(2).getName().equals("Sigourney Weaver"));
    assertTrue(path.get(3).getName().equals("William Finley"));
    assertTrue(path.get(4).getName().equals("Faye Dunaway"));
    assertTrue(path.get(5).getName().equals("Donald Sutherland"));
    assertTrue(path.get(6).getName().equals("Steve Lemme"));
  }

  /**
   * Helper function to set the connection to the appropriate database.
   *
   * @param db the database to connect to
   */
  private void setConnection(String db) {
    try {
      Class.forName("org.sqlite.JDBC");
      String url = "jdbc:sqlite:" + db;
      conn = DriverManager.getConnection(url);
      Statement stat = conn.createStatement();
      stat.executeUpdate("PRAGMA foreign_keys = ON;");
      stat.close();
    } catch (ClassNotFoundException e) {

    } catch (SQLException e) {
      System.out.println("ERROR: Cannot connect to " + db);
    }
  }

}
