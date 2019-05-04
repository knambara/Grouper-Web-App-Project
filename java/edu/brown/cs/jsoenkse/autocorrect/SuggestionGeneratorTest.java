package edu.brown.cs.jsoenkse.autocorrect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.jsoenkse.trees.Trie;

/**
 * This SuggestionGeneratorTest class is intended to test the functionality of
 * the SuggestionGenerator class.
 *
 * @author jennasoenksen
 */
public class SuggestionGeneratorTest {

  /**
   * Constructor for the SuggestionGeneratorTest class.
   */
  public SuggestionGeneratorTest() {

  }

  /**
   * Test for autocomplete function when prefix setting is off. Ensures function
   * only returns exact matches in the Trie.
   */
  @Test
  public void testPrefixOff() {
    Trie trie = new Trie();
    trie.insert("watermelon");
    SuggestionGenerator sg = new SuggestionGenerator(trie, new ACSettings());
    assertEquals(sg.autocomplete("water"), new ArrayList<String>());
    trie.insert("water");
    assertEquals(sg.autocomplete("water"),
        new ArrayList<String>(Arrays.asList("water")));
  }

  /**
   * Test for the autocomplete function when the prefix setting is on.
   */
  @Test
  public void testPrefixOn() {
    Trie trie = new Trie();
    ACSettings settings = new ACSettings();
    settings.setBoolSetting("prefix", true);
    trie.insert("watermelon");
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    assertEquals(sg.autocomplete("water"),
        new ArrayList<String>(Arrays.asList("watermelon")));
    trie.insert("waters");
    assertEquals(sg.autocomplete("water"),
        new ArrayList<String>(Arrays.asList("waters", "watermelon")));
  }

  /**
   * Test that the autocomplete function adequately adapts when the prefix
   * setting is changed between calls.
   */
  @Test
  public void testResultWhenPrefixIsChanged() {
    Trie trie = new Trie();
    ACSettings settings = new ACSettings();
    trie.insert("watermelon");
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    assertEquals(sg.autocomplete("water"), new ArrayList<String>());
    settings.setBoolSetting("prefix", true);
    assertEquals(sg.autocomplete("water"),
        new ArrayList<String>(Arrays.asList("watermelon")));
  }

  /**
   * Test to make sure using autocomplete on a word not in the Trie does not
   * raise errors and returns an empty list.
   */
  @Test
  public void testAutocompleteOnWordNotInTrie() {
    Trie trie = new Trie();
    trie.insert("bubble");
    ACSettings settings = new ACSettings();
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    settings.setBoolSetting("prefix", true);
    assertEquals(sg.autocomplete("potato"), new ArrayList<String>());
  }

  /**
   * Test to make sure using autocomplete on null returns an empty list without
   * raising errors.
   */
  @Test
  public void testAutocompleteOnNull() {
    Trie trie = new Trie();
    trie.insert("tea");
    SuggestionGenerator sg = new SuggestionGenerator(trie, new ACSettings());
    assertEquals(sg.autocomplete(null), new ArrayList<String>());
  }

  /**
   * Test for the autocorrect function with an LED of 2.
   */
  @Test
  public void testAutocorrectWithLED2() {
    Trie trie = new Trie();
    trie.insert("and");
    trie.insert("it");
    trie.insert("animal");
    trie.insert("panda");
    trie.insert("pan");
    trie.insert("pat");
    trie.insert("pal");
    trie.insert("i");
    trie.insert("a");
    trie.insert("an");
    ACSettings settings = new ACSettings();
    settings.setLED(2);
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    List<String> results = sg.autocorrect("ap");
    assertTrue(results.contains("and"));
    assertTrue(results.contains("it"));
    assertTrue(results.contains("pan"));
    assertTrue(results.contains("pat"));
    assertTrue(results.contains("pal"));
    assertTrue(results.contains("i"));
    assertTrue(results.contains("a"));
    assertTrue(results.contains("an"));
    assertFalse(results.contains("animal"));
    assertFalse(results.contains("panda"));
  }

  /**
   * Test of the autocorrect function with an LED of zero. A word is only
   * returned if it is an exact match to the function input.
   */
  @Test
  public void testAutocorrectWithLEDZero() {
    Trie trie = new Trie();
    trie.insert("bagel");
    trie.insert("pepper");
    SuggestionGenerator sg = new SuggestionGenerator(trie, new ACSettings());
    assertEquals(sg.autocorrect("egg"), new ArrayList<String>());
    assertEquals(sg.autocorrect("bagel"),
        new ArrayList<String>(Arrays.asList("bagel")));
  }

  /**
   * Test that the autocorrect function adequately adapts when the LED setting
   * is changed between calls.
   */
  @Test
  public void testAutocorrectWithChangingLED() {
    Trie trie = new Trie();
    trie.insert("pie");
    trie.insert("pig");
    trie.insert("egg");
    trie.insert("eager");
    ACSettings settings = new ACSettings();
    settings.setLED(4);
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    List<String> results1 = sg.autocorrect("fig");
    assertTrue(results1.contains("egg"));
    assertTrue(results1.contains("pig"));
    assertTrue(results1.contains("pie"));
    assertTrue(results1.contains("eager"));
    settings.setLED(1);
    List<String> results2 = sg.autocorrect("fig");
    assertTrue(results2.contains("pig"));
    assertFalse(results2.contains("egg"));
    assertFalse(results2.contains("pie"));
    assertFalse(results2.contains("eager"));
  }

  /**
   * Test to make sure that a null input for the autocorrect function returns an
   * empty list without raising errors.
   */
  @Test
  public void testAutocorrectOnNullInput() {
    SuggestionGenerator sg = new SuggestionGenerator(new Trie(),
        new ACSettings());
    assertEquals(sg.autocorrect(null), new ArrayList<String>());
  }

  /**
   * Test to make sure words are not split when whitespace setting is off.
   */
  @Test
  public void testWhitespaceOff() {
    Trie trie = new Trie();
    trie.insert("rain");
    trie.insert("bow");
    SuggestionGenerator sg = new SuggestionGenerator(trie, new ACSettings());
    assertEquals(sg.breakOnWhitespace("rainbow"), new ArrayList<String>());
  }

  /**
   * Test for a simple case where input should be split.
   */
  @Test
  public void testSimpleBreakOnWhitespace() {
    Trie trie = new Trie();
    trie.insert("egg");
    trie.insert("whites");
    trie.insert("eggery");
    ACSettings settings = new ACSettings();
    settings.setBoolSetting("whitespace", true);
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    assertEquals(sg.breakOnWhitespace("eggwhites"),
        new ArrayList<String>(Arrays.asList("egg whites")));
  }

  /**
   * Test to make sure a compound word splits if both parts of the word are in
   * the Trie.
   */
  @Test
  public void testCompoundWordBreakOnWhitespace() {
    Trie trie = new Trie();
    trie.insert("water");
    trie.insert("melon");
    ACSettings settings = new ACSettings();
    settings.setBoolSetting("whitespace", true);
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    assertEquals(sg.breakOnWhitespace("watermelon"),
        new ArrayList<String>(Arrays.asList("water melon")));
  }

  /**
   * This test makes sure that an empty list is returned when the
   * breakOnWhitespace input cannot be separated into two valid words.
   */
  @Test
  public void testBreakOnWhitespaceWhenOnlyOnePartInTrie() {
    Trie trie = new Trie();
    trie.insert("tea");
    trie.insert("bagel");
    ACSettings settings = new ACSettings();
    settings.setBoolSetting("whitespace", true);
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    assertEquals(sg.breakOnWhitespace("teabag"), new ArrayList<String>());
    assertEquals(sg.breakOnWhitespace("honeybee"), new ArrayList<String>());
  }

  /**
   * Simple test to ensure breakOnWhitespace returns and empty list and no
   * errors on null input.
   */
  @Test
  public void testBreakOnWhitespaceWithNullInput() {
    ACSettings settings = new ACSettings();
    settings.setBoolSetting("whitespace", true);
    SuggestionGenerator sg = new SuggestionGenerator(new Trie(), settings);
    assertEquals(sg.breakOnWhitespace(null), new ArrayList<String>());
  }

  /**
   * Test that the breakOnWhitespace function adequately adapts when the
   * whitespace setting is changed between calls.
   */
  @Test
  public void testWhitespaceSettingChanging() {
    Trie trie = new Trie();
    trie.insert("tea");
    trie.insert("bagel");
    trie.insert("sunglasses");
    trie.insert("brownie");
    ACSettings settings = new ACSettings();
    settings.setBoolSetting("whitespace", true);
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    assertEquals(sg.breakOnWhitespace("teabrownie"),
        new ArrayList<String>(Arrays.asList("tea brownie")));
    settings.setBoolSetting("whitespace", false);
    assertEquals(sg.breakOnWhitespace("teabrownie"), new ArrayList<String>());
  }

  /**
   * Test to make sure that three valid words don't split when either side of
   * the split is invalid.
   */
  @Test
  public void testThreeValidWordsTogetherDoesntSplit() {
    Trie trie = new Trie();
    trie.insert("paper");
    trie.insert("spoon");
    trie.insert("phone");
    trie.insert("brush");
    ACSettings settings = new ACSettings();
    settings.setBoolSetting("whitespace", true);
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    assertEquals(sg.breakOnWhitespace("phonespoonbrush"),
        new ArrayList<String>());
  }

  /**
   * This test makes sure that all possible valid splits are returned.
   */
  @Test
  public void testAllOptionsReturned() {
    Trie trie = new Trie();
    trie.insert("milk");
    trie.insert("milkshake");
    trie.insert("milkshakes");
    trie.insert("spear");
    trie.insert("pear");
    trie.insert("shakespear");
    ACSettings settings = new ACSettings();
    settings.setBoolSetting("whitespace", true);
    SuggestionGenerator sg = new SuggestionGenerator(trie, settings);
    List<String> results = sg.breakOnWhitespace("milkshakespear");
    assertTrue(results.contains("milkshake spear"));
    assertTrue(results.contains("milkshakes pear"));
    assertTrue(results.contains("milk shakespear"));
  }
}
