package edu.brown.cs.jsoenkse.textAnalysis;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

/**
 * This ComparatorsTest class is intended to test the functionality of the
 * UnigramComparator, BigramComparator, and AlphabeticalComparator classes.
 *
 * @author jennasoenksen
 */
public class ComparatorsTest {

  /**
   * Simple test to make sure the UnigramComparator returns the expected order,
   * and that including unigrams not in the corpus does not throw errors.
   */
  @Test
  public void testUnigramComparator() {
    CorpusProcessor cp = new CorpusProcessor();
    cp.analyzeCorpus("data/autocorrect/restaurants.txt");
    HashMap<String, Integer> unigramMap = cp.getUnigramMap();
    List<String> toSort = new ArrayList<String>(
        Arrays.asList("and", "cafe", "turtle", "good"));
    Collections.sort(toSort, new UnigramComparator(unigramMap));
    assertEquals(toSort.get(0), "cafe");
    assertEquals(toSort.get(1), "and");
    assertEquals(toSort.get(2), "good");
    assertEquals(toSort.get(3), "turtle");
  }

  /**
   * Test to make sure the UnigramComparator only compares the first word in a
   * string.
   */
  @Test
  public void testUnigramComparatorOnTwoWordStrings() {
    CorpusProcessor cp = new CorpusProcessor();
    cp.analyzeCorpus("data/autocorrect/restaurants.txt");
    HashMap<String, Integer> unigramMap = cp.getUnigramMap();
    List<String> toSort = new ArrayList<String>(Arrays.asList("and jerrys",
        "starbucks cafe", "good turtle", "angry thai"));
    Collections.sort(toSort, new UnigramComparator(unigramMap));
    assertEquals(toSort.get(0), "and jerrys");
    assertEquals(toSort.get(1), "starbucks cafe");
    assertEquals(toSort.get(2), "good turtle");
    assertEquals(toSort.get(3), "angry thai");
  }

  /**
   * Simple test to make sure the BigramComparator returns the expected order,
   * and that including bigrams not in the corpus does not throw errors.
   */
  @Test
  public void testBigramComparator() {
    CorpusProcessor cp = new CorpusProcessor();
    cp.analyzeCorpus("data/autocorrect/restaurants.txt");
    HashMap<String, Integer> bigramMap = cp.getBigramMap();
    List<String> toSort = new ArrayList<String>(
        Arrays.asList("meeting", "mikes", "tea", "paragon"));
    Collections.sort(toSort, new BigramComparator(bigramMap, "cafe"));
    assertEquals(toSort.get(0), "mikes");
    assertEquals(toSort.get(1), "paragon");
    assertEquals(toSort.get(2), "meeting");
    assertEquals(toSort.get(3), "tea");
  }

  /**
   * Test to make sure the BigramComparator only compares the first words in a
   * string.
   */
  @Test
  public void testBigramComparatorOnTwoWordStrings() {
    CorpusProcessor cp = new CorpusProcessor();
    cp.analyzeCorpus("data/autocorrect/restaurants.txt");
    HashMap<String, Integer> bigramMap = cp.getBigramMap();
    List<String> toSort = new ArrayList<String>(Arrays.asList("jersey mikes",
        "paragon night jam", "smoothie cafe", "mikes ice"));
    Collections.sort(toSort, new BigramComparator(bigramMap, "cafe"));
    assertEquals(toSort.get(0), "paragon night jam");
    assertEquals(toSort.get(1), "mikes ice");
    assertEquals(toSort.get(2), "jersey mikes");
    assertEquals(toSort.get(3), "smoothie cafe");
  }

  /**
   * Simple test to make sure the AlphabeticalComparator returns a list of
   * Strings in alphabetical order.
   */
  @Test
  public void testAlphabeticalComparator() {
    List<String> toSort = new ArrayList<String>(
        Arrays.asList("peanut butter", "water", "accents", "pineapple"));
    Collections.sort(toSort, new AlphabeticalComparator());
    assertEquals(toSort.get(0), "accents");
    assertEquals(toSort.get(1), "peanut butter");
    assertEquals(toSort.get(2), "pineapple");
    assertEquals(toSort.get(3), "water");
  }

}
