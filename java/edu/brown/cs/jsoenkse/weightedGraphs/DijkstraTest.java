package edu.brown.cs.jsoenkse.weightedGraphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * This DijkstraTest class tests the functionality of the Dijkstra class,
 * confirming shortest paths are found in various scenarios.
 *
 * @author jennasoenksen
 */
public class DijkstraTest {

  /**
   * Simplest test possible, finding shortest (and only path) between two
   * vertices.
   */
  @Test
  public void testTwoVertexGraph() {
    TestVertex v1 = new TestVertex();
    TestVertex v2 = new TestVertex();
    TestEdge e1 = new TestEdge(v1, v2, 22);
    Set<TestEdge> edges = new HashSet<TestEdge>();
    edges.add(e1);
    v1.setEdges(edges);
    v2.setEdges(edges);
    Dijkstra<TestVertex, TestEdge> dk = new Dijkstra<TestVertex, TestEdge>();
    List<TestVertex> path = dk.shortestPath(v1, v2);
    assertEquals(path.get(0), v1);
    assertEquals(path.get(1), v2);
  }

  /**
   * Test for the case of finding the shortest path between the same vertex.
   */
  @Test
  public void testPathBetweenSameVertex() {
    TestVertex v1 = new TestVertex();
    Dijkstra<TestVertex, TestEdge> dk = new Dijkstra<TestVertex, TestEdge>();
    List<TestVertex> path = dk.shortestPath(v1, v1);
    assertEquals(path.get(0), v1);
  }

  /**
   * Test for a simple three vertex graph where shortest path is two edges.
   */
  @Test
  public void testThreeVertexGraph() {
    TestVertex v1 = new TestVertex();
    TestVertex v2 = new TestVertex();
    TestVertex v3 = new TestVertex();
    TestEdge e1 = new TestEdge(v1, v2, 22);
    TestEdge e2 = new TestEdge(v1, v3, 15);
    TestEdge e3 = new TestEdge(v2, v3, 3);
    Set<TestEdge> edgeSet1 = new HashSet<TestEdge>();
    edgeSet1.add(e1);
    edgeSet1.add(e2);
    v1.setEdges(edgeSet1);
    Set<TestEdge> edgeSet2 = new HashSet<TestEdge>();
    edgeSet2.add(e1);
    edgeSet2.add(e3);
    v2.setEdges(edgeSet2);
    Set<TestEdge> edgeSet3 = new HashSet<TestEdge>();
    edgeSet3.add(e2);
    edgeSet3.add(e3);
    v3.setEdges(edgeSet3);
    Dijkstra<TestVertex, TestEdge> dk = new Dijkstra<TestVertex, TestEdge>();
    List<TestVertex> path = dk.shortestPath(v1, v2);
    assertEquals(path.size(), 3);
    assertEquals(path.get(0), v1);
    assertEquals(path.get(1), v3);
    assertEquals(path.get(2), v2);
  }

  /**
   * Test to make sure trying to find the shortest path to a disconnected vertex
   * returns an empty list.
   */
  @Test
  public void testPathToDisconnectedVertex() {
    TestVertex v1 = new TestVertex();
    TestVertex v2 = new TestVertex();
    TestVertex v3 = new TestVertex();
    TestEdge e1 = new TestEdge(v1, v2, 22);
    TestEdge e2 = new TestEdge(v1, v3, 15);
    Set<TestEdge> edgeSet1 = new HashSet<TestEdge>();
    edgeSet1.add(e1);
    v1.setEdges(edgeSet1);
    v2.setEdges(edgeSet1);
    Dijkstra<TestVertex, TestEdge> dk = new Dijkstra<TestVertex, TestEdge>();
    List<TestVertex> path = dk.shortestPath(v1, v3);
    assertEquals(path.size(), 0);
  }

  /**
   * This test offers a graph that has two shortest paths.
   */
  @Test
  public void testGraphWithMultipleShortPaths() {
    TestVertex v1 = new TestVertex();
    TestVertex v2 = new TestVertex();
    TestVertex v3 = new TestVertex();
    TestVertex v4 = new TestVertex();
    TestEdge e1 = new TestEdge(v1, v2, 3);
    TestEdge e2 = new TestEdge(v2, v3, 7);
    TestEdge e3 = new TestEdge(v3, v4, 5);
    TestEdge e4 = new TestEdge(v4, v1, 5);
    TestEdge e5 = new TestEdge(v1, v3, 12);
    Set<TestEdge> edgeSet1 = new HashSet<TestEdge>();
    edgeSet1.add(e1);
    edgeSet1.add(e4);
    edgeSet1.add(e5);
    v1.setEdges(edgeSet1);
    Set<TestEdge> edgeSet2 = new HashSet<TestEdge>();
    edgeSet2.add(e1);
    edgeSet2.add(e2);
    v2.setEdges(edgeSet2);
    Set<TestEdge> edgeSet3 = new HashSet<TestEdge>();
    edgeSet3.add(e2);
    edgeSet3.add(e3);
    edgeSet3.add(e5);
    v3.setEdges(edgeSet3);
    Set<TestEdge> edgeSet4 = new HashSet<TestEdge>();
    edgeSet4.add(e3);
    edgeSet4.add(e4);
    v4.setEdges(edgeSet4);
    Dijkstra<TestVertex, TestEdge> dk = new Dijkstra<TestVertex, TestEdge>();
    List<TestVertex> path = dk.shortestPath(v1, v3);
    assertEquals(path.size(), 3);
    assertEquals(path.get(0), v1);
    assertTrue(path.get(1) == v2 || path.get(1) == v4);
    assertEquals(path.get(2), v3);
  }

  /**
   * This test creates a simple 3 vertex DAG to make sure Dijkstra's handles it
   * accordingly.
   */
  @Test
  public void testSimpleDAG() {
    TestVertex v1 = new TestVertex();
    TestVertex v2 = new TestVertex();
    TestVertex v3 = new TestVertex();
    TestEdge e1 = new TestEdge(v1, v3, 10);
    TestEdge e2 = new TestEdge(v1, v2, 3);
    TestEdge e3 = new TestEdge(v2, v3, 4);
    TestEdge e4 = new TestEdge(v3, v1, 3);
    Set<TestEdge> edgeSet1 = new HashSet<TestEdge>();
    edgeSet1.add(e1);
    edgeSet1.add(e2);
    v1.setEdges(edgeSet1);
    Set<TestEdge> edgeSet2 = new HashSet<TestEdge>();
    edgeSet2.add(e3);
    v2.setEdges(edgeSet2);
    Set<TestEdge> edgeSet3 = new HashSet<TestEdge>();
    edgeSet3.add(e4);
    v3.setEdges(edgeSet3);
    Dijkstra<TestVertex, TestEdge> dk = new Dijkstra<TestVertex, TestEdge>();
    List<TestVertex> path = dk.shortestPath(v1, v3);
    assertEquals(path.size(), 3);
    assertEquals(path.get(0), v1);
    assertEquals(path.get(1), v2);
    assertEquals(path.get(2), v3);
  }
}

/**
 * Inner class to implement the Vertex interface for testing purposes.
 *
 * @author jennasoenksen
 */
class TestVertex implements Vertex<TestVertex, TestEdge> {

  private Set<TestEdge> edges;
  private TestVertex prev;
  private double dist;
  private boolean visited;

  TestVertex() {
    edges = new HashSet<TestEdge>();
    prev = null;
    dist = Double.MAX_VALUE;
    visited = false;
  }

  @Override
  public Set<TestEdge> getEdges() {
    return edges;
  }

  public void setEdges(Set<TestEdge> edges) {
    this.edges = edges;
  }

  @Override
  public void setPrev(TestVertex prev) {
    this.prev = prev;
  }

  @Override
  public TestVertex getPrev() {
    return prev;
  }

  @Override
  public void setDistance(double dist) {
    this.dist = dist;
  }

  @Override
  public double getDistance() {
    return dist;
  }

  @Override
  public void setVisited(boolean visited) {
    this.visited = visited;
  }

  @Override
  public boolean getVisited() {
    return visited;
  }

  @Override
  public boolean isSame(TestVertex vert) {
    return this == vert;
  }
}

/**
 * Inner class to implement the Edge interface for testing purposes.
 *
 * @author jennasoenksen
 */
class TestEdge implements Edge<TestVertex, TestEdge> {

  private TestVertex v1;
  private TestVertex v2;
  private double weight;

  TestEdge(TestVertex v1, TestVertex v2, double weight) {
    this.v1 = v1;
    this.v2 = v2;
    this.weight = weight;
  }

  @Override
  public double getWeight() {
    return weight;
  }

  @Override
  public TestVertex opposite(TestVertex vert1) {
    if (vert1 == v1) {
      return v2;
    } else if (vert1 == v2) {
      return v1;
    } else {
      return null;
    }
  }
}
