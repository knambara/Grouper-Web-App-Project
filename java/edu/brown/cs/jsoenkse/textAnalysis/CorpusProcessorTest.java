package edu.brown.cs.jsoenkse.textAnalysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

/**
 * This CorpusProcessorTest is designed to test the functionality of the
 * CorpusProcessor class.
 *
 * @author jennasoenksen
 */
public class CorpusProcessorTest {

  /**
   * Class constructor.
   */
  public CorpusProcessorTest() {

  }

  /**
   * Test for the function of the unigram map.
   */
  @Test
  public void testUnigramMap() {
    CorpusProcessor cp = new CorpusProcessor();
    cp.analyzeCorpus("data/autocorrect/restaurants.txt");
    HashMap<String, Integer> unigramMap = cp.getUnigramMap();
    assertTrue(unigramMap.containsKey("pockets"));
    assertTrue(unigramMap.get("cafe") == 3);
    assertTrue(unigramMap.containsKey("b"));
    assertFalse(unigramMap.containsKey("ratty"));
  }

  /**
   * Test for the function of the bigram map.
   */
  @Test
  public void testBigramMap() {
    CorpusProcessor cp = new CorpusProcessor();
    cp.analyzeCorpus("data/autocorrect/restaurants.txt");
    HashMap<String, Integer> bigramMap = cp.getBigramMap();
    assertTrue(bigramMap.get("kung fu") == 1);
    assertTrue(bigramMap.get("curry antonios") == 1);
    assertTrue(bigramMap.containsKey("jerrys cafe"));
    assertTrue(bigramMap.containsKey("cafe paragon"));
    assertTrue(bigramMap.containsKey("cafe mikes"));
    assertFalse(bigramMap.containsKey("ben jerrys"));
  }

  /**
   * This test makes sure that the CorpusProcessor can read multiple files and
   * build upon the same Trie instead of overriding.
   */
  @Test
  public void testMultipleCorpusAddToTrie() {
    CorpusProcessor cp = new CorpusProcessor();
    cp.analyzeCorpus("data/autocorrect/restaurants.txt");
    int oldSize = cp.getTrie().getSize();
    cp.analyzeCorpus("data/autocorrect/sherlock.txt");
    int newSize = cp.getTrie().getSize();
    assertTrue(oldSize < newSize);
  }
}
