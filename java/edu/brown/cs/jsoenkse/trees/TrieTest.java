package edu.brown.cs.jsoenkse.trees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * This TrieTest class is intended to test the functionality of the Trie class.
 *
 * @author jennasoenksen
 */
public class TrieTest {

  /**
   * Constructor for the TrieTest class.
   */
  public TrieTest() {

  }

  /**
   * Test to make sure words are inserted properly into the Trie by checking how
   * many nodes (excluding the root) are in the Trie.
   */
  @Test
  public void testSimpleInsert() {
    Trie trie = new Trie();
    trie.insert("hello");
    assertEquals(trie.getSize(), 5);
    trie.insert("helios");
    assertEquals(trie.getSize(), 8);
    trie.insert("inch");
    assertEquals(trie.getSize(), 12);
    trie.insert("intro");
    assertEquals(trie.getSize(), 15);
    trie.insert("introduce");
    assertEquals(trie.getSize(), 19);
  }

  /**
   * This test makes sure that inserting a word that is the prefix of an
   * existing word in the Trie does not affect the size of the Trie.
   */
  @Test
  public void testInsertingPrefixOfWordDoesntChangeSize() {
    Trie trie = new Trie();
    trie.insert("watermelon");
    int oldSize = trie.getSize();
    trie.insert("water");
    assertEquals(trie.getSize(), oldSize);
  }

  /**
   * This test makes sure that attempting to insert the same word twice does not
   * affect the size of the Trie.
   */
  @Test
  public void testDuplicateInserts() {
    Trie trie = new Trie();
    trie.insert("turtles");
    assertTrue(trie.getSize() == 7);
    trie.insert("turtles");
    assertTrue(trie.getSize() == 7);
    assertTrue(trie.find("turtles").isTerminal());
  }

  /**
   * This is a simple test to check that the find function returns the expected
   * TrieNode.
   */
  @Test
  public void testSimpleFind() {
    Trie trie = new Trie();
    trie.insert("banana");
    trie.insert("eggs");
    trie.insert("bacon");
    assertTrue(trie.find("eggs").isTerminal());
    assertTrue(trie.find("banana").isTerminal());
    assertTrue(trie.find("bacon").isTerminal());
    assertFalse(trie.find("bana").isTerminal());
  }

  /**
   * This simple test makes sure that the find method returns nodes denoting the
   * end of a word only for words that have been explicitly added.
   */
  @Test
  public void testInsertAndFindWithPrefix() {
    Trie trie = new Trie();
    trie.insert("watermelon");
    assertFalse(trie.find("water").isTerminal());
    trie.insert("water");
    assertTrue(trie.find("water").isTerminal());
  }

  /**
   * Test to make sure attempting to find words not in the Trie returns null.
   */
  @Test
  public void testFindOnWordNotInTrie() {
    Trie trie = new Trie();
    trie.insert("pancake");
    assertEquals(trie.find("apple"), null);
  }

  /**
   * Test to ensure upper and lower case characters are treated as distinct in
   * the Trie.
   */
  @Test
  public void testInsertAndFindWithMixedCase() {
    Trie trie = new Trie();
    trie.insert("Hello");
    trie.insert("HELLO");
    assertTrue(trie.getSize() == 9);
    trie.insert("hello");
    assertTrue(trie.getSize() == 14);
    trie.insert("heLLo");
    assertTrue(trie.getSize() == 17);
    assertTrue(trie.find("He") != null);
    assertTrue(trie.find("hE") == null);
    assertTrue(trie.find("HELLO") != null);
    assertTrue(trie.find("hEllO") == null);
  }

  /**
   * Simple test to make sure special characters are properly handled by the
   * Trie.
   */
  @Test
  public void testInsertAndFindWithSpecialChars() {
    Trie trie = new Trie();
    trie.insert("build-up");
    trie.insert("build!up");
    assertTrue(trie.getSize() == 11);
    trie.insert("#blessed");
    assertTrue(trie.getSize() == 19);
    assertTrue(trie.find("#") != null);
    assertTrue(trie.find("build!") != null);
  }

  /**
   * Simple test to make sure numerical characters can be added to a Trie.
   */
  @Test
  public void testTrieWithNumbers() {
    Trie trie = new Trie();
    trie.insert("12345");
    trie.insert("1357");
    trie.insert("13");
    assertTrue(trie.getSize() == 8);
    assertTrue(trie.find("13").isTerminal());
    assertTrue(trie.find("357") == null);
  }

  /**
   * This test ensures that any spaces included in entries are treated as
   * distinct characters in the Trie.
   */
  @Test
  public void testMultiwordInput() {
    Trie trie = new Trie();
    trie.insert("sea turtle");
    assertTrue(trie.getSize() == 10);
    trie.insert("sea shell");
    assertTrue(trie.getSize() == 15);
    trie.insert("seaside");
    assertTrue(trie.getSize() == 19);
  }
}
