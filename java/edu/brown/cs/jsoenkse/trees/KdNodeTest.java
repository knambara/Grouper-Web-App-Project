package edu.brown.cs.jsoenkse.trees;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * This KdNodeTest class is intended to test functionality of KdNode class.
 *
 * @author jennasoenksen
 */
public class KdNodeTest {

  /**
   * Test getLocation() method.
   */
  @Test
  public void testLocation() {
    ArrayList<Double> loc = new ArrayList<Double>(Arrays.asList(1., 2., 3.));
    KdNode n = new KdNode(loc);
    assertEquals(n.getLocation(), loc);
  }

  /**
   * Test AssertionError is raised when inputting null.
   */
  @Test(expected = AssertionError.class)
  public void testNullInput() {
    new KdNode(null);
  }

  /**
   * Test that the setLeft() and getLeft() methods work as expected.
   */
  @Test
  public void testLeft() {
    KdNode parent = new KdNode(
        new ArrayList<Double>(Arrays.asList(1., 2., 3.)));
    KdNode left = new KdNode(
        new ArrayList<Double>(Arrays.asList(7.7, 2.2, 0.1)));
    parent.setLeft(left);
    assertEquals(parent.getLeft(), left);
  }

  /**
   * Test that the setRight() and getRight() methods work as expected.
   */
  @Test
  public void testRight() {
    KdNode parent = new KdNode(
        new ArrayList<Double>(Arrays.asList(1.0001, 2.231, 3.23542)));
    KdNode right = new KdNode(
        new ArrayList<Double>(Arrays.asList(7.7, 2.2, 0.1)));
    parent.setLeft(right);
    assertEquals(parent.getLeft(), right);
  }
}
