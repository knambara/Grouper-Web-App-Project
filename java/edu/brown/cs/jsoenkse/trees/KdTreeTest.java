package edu.brown.cs.jsoenkse.trees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Class to test the functionality of the KdTreeTest class.
 *
 * @author jennasoenksen
 */
public class KdTreeTest {

  /**
   * Test to make sure a single point is properly inserted into the tree.
   */
  @Test
  public void testSingleInsert() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(1., 2., 3.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    KdNode n1 = tree.build(points, 0);
    assertEquals(tree.size(), 1);
    assertEquals(n1.getLocation(), loc);
  }

  /**
   * Test to make sure three points are properly insterted into the tree.
   */
  @Test
  public void testThreeNodeTree() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    KdNode root = tree.build(points, 0);
    assertEquals(tree.size(), 3);
    assertEquals(root.getLocation(), loc);
    assertEquals(root.getLeft().getLocation(), loc2);
    assertEquals(root.getRight().getLocation(), loc3);
  }

  /**
   * Test to make sure the sortPointsBy helper method sorts points based on the
   * x dimension, as expected.
   */
  @Test
  public void testSortByXDim() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    List<ArrayList<Double>> sortedPoints = tree.sortPointsBy(points, 0);
    assertEquals(sortedPoints.get(0), loc2);
    assertEquals(sortedPoints.get(1), loc);
    assertEquals(sortedPoints.get(2), loc3);
  }

  /**
   * Test to make sure the sortPointsBy helper method sorts points based on the
   * y dimension, as expected.
   */
  @Test
  public void testSortByYDim() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    List<ArrayList<Double>> sortedPoints = tree.sortPointsBy(points, 1);
    assertEquals(sortedPoints.get(0), loc3);
    assertEquals(sortedPoints.get(1), loc);
    assertEquals(sortedPoints.get(2), loc2);
  }

  /**
   * Test to make sure the sortPointsBy helper method sorts points based on the
   * z dimension, as expected.
   */
  @Test
  public void testSortByZDim() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    List<ArrayList<Double>> sortedPoints = tree.sortPointsBy(points, 2);
    assertEquals(sortedPoints.get(0), loc);
    assertEquals(sortedPoints.get(1), loc3);
    assertEquals(sortedPoints.get(2), loc2);
  }

  /**
   * Test to make sure that a three level, 7 node tree is built as expected,
   * with the third level being sorted based on the y dimension.
   */
  @Test
  public void testThreeLevelTreeSortedByYDim() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(5., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(4., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<Double> loc4 = new ArrayList<Double>(Arrays.asList(3., 2., 11.));
    ArrayList<Double> loc5 = new ArrayList<Double>(Arrays.asList(1., 12., 6.));
    ArrayList<Double> loc6 = new ArrayList<Double>(Arrays.asList(2., 23., 1.));
    ArrayList<Double> loc7 = new ArrayList<Double>(Arrays.asList(8., 7., 21.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    points.add(loc4);
    points.add(loc5);
    points.add(loc6);
    points.add(loc7);
    KdNode root = tree.build(points, 0);
    assertEquals(root.getLocation(), loc2);
    assertEquals(root.getLeft().getLocation(), loc5);
    assertEquals(root.getRight().getLocation(), loc);
    assertEquals(root.getLeft().getLeft().getLocation(), loc4);
    assertEquals(root.getLeft().getRight().getLocation(), loc6);
    assertEquals(root.getRight().getLeft().getLocation(), loc3);
    assertEquals(root.getRight().getRight().getLocation(), loc7);
  }

  /**
   * Test to make sure that a four level, 10 node tree is built as expected,
   * with the fourth level being sorted based on the z dimension.
   */
  @Test
  public void testFourLevelTreeSortedByZDim() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(5., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(4., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<Double> loc4 = new ArrayList<Double>(Arrays.asList(3., 2., 11.));
    ArrayList<Double> loc5 = new ArrayList<Double>(Arrays.asList(1., 12., 6.));
    ArrayList<Double> loc6 = new ArrayList<Double>(Arrays.asList(2., 23., 1.));
    ArrayList<Double> loc7 = new ArrayList<Double>(Arrays.asList(8., 7., 21.));
    ArrayList<Double> loc8 = new ArrayList<Double>(Arrays.asList(42., 0., 2.));
    ArrayList<Double> loc9 = new ArrayList<Double>(Arrays.asList(3., 133., 5.));
    ArrayList<Double> loc10 = new ArrayList<Double>(
        Arrays.asList(0.2, 6., 231.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    points.add(loc4);
    points.add(loc5);
    points.add(loc6);
    points.add(loc7);
    points.add(loc8);
    points.add(loc9);
    points.add(loc10);
    KdNode root = tree.build(points, 0);
    assertEquals(tree.size(), 10);
    assertEquals(root.getLocation(), loc2);
    assertEquals(root.getLeft().getLocation(), loc5);
    assertEquals(root.getRight().getLocation(), loc);
    assertEquals(root.getLeft().getLeft().getLocation(), loc10);
    assertEquals(root.getLeft().getRight().getLocation(), loc9);
    assertEquals(root.getRight().getLeft().getLocation(), loc3);
    assertEquals(root.getRight().getRight().getLocation(), loc7);
    assertEquals(root.getLeft().getLeft().getLeft().getLocation(), loc4);
    assertEquals(root.getLeft().getRight().getLeft().getLocation(), loc6);
  }

  /**
   * Test to make sure nNearestNeighbors appropriately finds the two nearest
   * neighbors in a three node tree.
   */
  @Test
  public void nNearestNeighborsTestOnThreeNodeTree() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(Arrays.asList(7., 1., 9.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.nNearestNeighbors(root, target, neighbors, 2);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertTrue(neighborLocs.contains(loc));
    assertTrue(neighborLocs.contains(loc3));
    assertFalse(neighborLocs.contains(loc2));
  }

  /**
   * Test to make sure nNearestNeighbors returns an empty list when n=0.
   */
  @Test
  public void testZeroNearestNeighbors() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(Arrays.asList(7., 1., 9.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.nNearestNeighbors(root, target, neighbors, 0);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertFalse(neighborLocs.contains(loc));
    assertFalse(neighborLocs.contains(loc3));
    assertFalse(neighborLocs.contains(loc2));
  }

  /**
   * Test to make sure nNearestNeighbors method recurs down the appropriate
   * subtrees when there is potential for a neighbor to exist in that subtree.
   */
  @Test
  public void threeNearestNeighborsInThreeLevelTree() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(5., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(4., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<Double> loc4 = new ArrayList<Double>(Arrays.asList(3., 2., 11.));
    ArrayList<Double> loc5 = new ArrayList<Double>(Arrays.asList(1., 12., 6.));
    ArrayList<Double> loc6 = new ArrayList<Double>(Arrays.asList(2., 23., 1.));
    ArrayList<Double> loc7 = new ArrayList<Double>(Arrays.asList(8., 7., 21.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    points.add(loc4);
    points.add(loc5);
    points.add(loc6);
    points.add(loc7);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(Arrays.asList(0., 0., 0.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.nNearestNeighbors(root, target, neighbors, 3);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertEquals(neighborLocs.get(0), loc);
    assertEquals(neighborLocs.get(1), loc3);
    assertEquals(neighborLocs.get(2), loc4);
  }

  /**
   * Test to make sure nothing breaks when the number of neighbors is more than
   * the total nodes in the tree, and that instead all nodes are returned, in
   * order to distance.
   */
  @Test
  public void returnAllNeighborsWhenLessNodesThanNTest() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(Arrays.asList(7., 1., 9.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.nNearestNeighbors(root, target, neighbors, 5);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertEquals(neighborLocs.size(), 3);
    assertEquals(neighborLocs.get(0), loc3);
    assertEquals(neighborLocs.get(1), loc);
    assertEquals(neighborLocs.get(2), loc2);
  }

  /**
   * Test to ensure the nNearestNeighbors still returns the expected neighbors
   * even when the target location is the root of the tree.
   */
  @Test
  public void nearestNeighborsWhereTargetIsRoot() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.nNearestNeighbors(root, target, neighbors, 2);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertEquals(neighborLocs.get(0), loc);
    assertEquals(neighborLocs.get(1), loc3);
  }

  /**
   * Test to make sure that if two nodes are of equal distance but only one can
   * be included in the neighbors list, then one and only one of them is
   * included.
   */
  @Test
  public void nearestNeighborsWithEqualDistance() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(1., 1., 1.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 3., 3.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 2., 3.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(Arrays.asList(2., 2., 2.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.nNearestNeighbors(root, target, neighbors, 1);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertEquals(neighborLocs.size(), 1);
    assertTrue(neighborLocs.contains(loc) || neighborLocs.contains(loc2));
  }

  /**
   * Test to make sure the radius method works for a target that is a location
   * in the tree, and that the results are returned in order of closest to
   * furthest.
   */
  @Test
  public void radiusAroundPositionInTree() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(0., 0., 0.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(4., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<Double> loc4 = new ArrayList<Double>(Arrays.asList(3., 2., 11.));
    ArrayList<Double> loc5 = new ArrayList<Double>(Arrays.asList(1., 12., 6.));
    ArrayList<Double> loc6 = new ArrayList<Double>(Arrays.asList(2., 23., 1.));
    ArrayList<Double> loc7 = new ArrayList<Double>(Arrays.asList(8., 7., 21.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    points.add(loc4);
    points.add(loc5);
    points.add(loc6);
    points.add(loc7);
    KdNode root = tree.build(points, 0);
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.radius(root, loc, neighbors, 13);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertEquals(neighborLocs.size(), 4);
    assertEquals(neighborLocs.get(0), loc);
    assertEquals(neighborLocs.get(1), loc3);
    assertEquals(neighborLocs.get(2), loc4);
    assertEquals(neighborLocs.get(3), loc2);
  }

  /**
   * Test to make sure that using the radius method with a radius size of zero
   * works only if the target position is in the tree.
   */
  @Test
  public void radiusOfZeroWorksIfPositionIsInTree() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.radius(root, target, neighbors, 0);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertEquals(neighborLocs.size(), 1);
    assertTrue(neighborLocs.contains(loc2));
    assertFalse(neighborLocs.contains(loc));
    assertFalse(neighborLocs.contains(loc3));
  }

  /**
   * Test to make sure that using the radius method with a radius size of zero
   * returns no nodes when the target position is not in the tree.
   */
  @Test
  public void radiusOfZeroDoesntWorkIfPositionIsNotInTree() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(4., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(3., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(
        Arrays.asList(3.1, 8., 9.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.radius(root, target, neighbors, 0);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertEquals(neighborLocs.size(), 0);
    assertFalse(neighborLocs.contains(loc2));
    assertFalse(neighborLocs.contains(loc));
    assertFalse(neighborLocs.contains(loc3));
  }

  /**
   * General test of the radius function, making sure that only some nodes on
   * different subtrees are found in the specified area, and that they are
   * returned in the proper order.
   */
  @Test
  public void testGeneralRadiusFunction() {
    KdTree tree = new KdTree(3);
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(5., 2., 3.));
    ArrayList<Double> loc2 = new ArrayList<Double>(Arrays.asList(4., 8., 9.));
    ArrayList<Double> loc3 = new ArrayList<Double>(Arrays.asList(7., 1., 8.));
    ArrayList<Double> loc4 = new ArrayList<Double>(Arrays.asList(3., 2., 11.));
    ArrayList<Double> loc5 = new ArrayList<Double>(Arrays.asList(1., 12., 6.));
    ArrayList<Double> loc6 = new ArrayList<Double>(Arrays.asList(2., 23., 1.));
    ArrayList<Double> loc7 = new ArrayList<Double>(Arrays.asList(8., 7., 21.));
    ArrayList<ArrayList<Double>> points = new ArrayList<ArrayList<Double>>();
    points.add(loc);
    points.add(loc2);
    points.add(loc3);
    points.add(loc4);
    points.add(loc5);
    points.add(loc6);
    points.add(loc7);
    KdNode root = tree.build(points, 0);
    ArrayList<Double> target = new ArrayList<Double>(Arrays.asList(0., 0., 0.));
    ArrayList<KdNode> neighbors = new ArrayList<KdNode>();
    neighbors = tree.radius(root, target, neighbors, 13);
    ArrayList<ArrayList<Double>> neighborLocs = new ArrayList<ArrayList<Double>>();
    for (KdNode n : neighbors) {
      neighborLocs.add(n.getLocation());
    }
    assertEquals(neighborLocs.size(), 4);
    assertEquals(neighborLocs.get(0), loc);
    assertEquals(neighborLocs.get(1), loc3);
    assertEquals(neighborLocs.get(2), loc4);
    assertEquals(neighborLocs.get(3), loc2);
  }
}
