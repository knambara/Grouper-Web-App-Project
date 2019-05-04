package edu.brown.cs.jsoenkse.stars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

/**
 * This is a class to test the functionality of the StarsParser class.
 *
 * @author jennasoenksen
 */
public class StarsParserTest {

  /**
   * Test to make sure the checkHeader() method returns true when expected.
   */
  @Test
  public void testValidHeader() {
    StarsParser reader = new StarsParser();
    String[] splitHeader = new String[] {
        "StarID", "ProperName", "X", "Y", "Z"
    };
    String[] headerTitles = new String[] {
        "StarID", "ProperName", "X", "Y", "Z"
    };
    assertTrue(reader.checkHeader(splitHeader, headerTitles));
  }

  /**
   * Test to make sure the checkHeader() method returns false when expected.
   */
  @Test
  public void testInvalidHeader1() {
    StarsParser reader = new StarsParser();
    String[] splitHeader = new String[] {
        "StarID", "ProperName", "X", "Y", "Z"
    };
    String[] headerTitles = new String[] {
        "StarID", "Name", "X", "Y", "Z"
    };
    assertFalse(reader.checkHeader(splitHeader, headerTitles));
  }

  /**
   * Test to make sure the checkHeader() method returns false when expected.
   */
  @Test
  public void testInvalidHeader2() {
    StarsParser reader = new StarsParser();
    String[] splitHeader = new String[] {
        "IDs", "ProperName", "X", "Y", "Z"
    };
    String[] headerTitles = new String[] {
        "StarID", "Name", "X", "Y", "Z"
    };
    assertFalse(reader.checkHeader(splitHeader, headerTitles));
  }

  /**
   * Test to ensure that a single star is added to the ArrayList by the Parser.
   */
  @Test
  public void testOneStarRead() {
    StarsParser reader = new StarsParser();
    ArrayList<Star> stars;
    try {
      stars = reader.readCSV("data/stars/one-star.csv", new String[] {
          "StarID", "ProperName", "X", "Y", "Z"
      });
      assertEquals(stars.size(), 1);
    } catch (IOException e) {
      System.out.println("ERROR: There was a problem reading this file.");
    }
  }

  /**
   * Test to ensure that all ten stars are added to the ArrayList by the Parser.
   */
  @Test
  public void testTenStarsRead() {
    StarsParser reader = new StarsParser();
    ArrayList<Star> stars;
    try {
      stars = reader.readCSV("data/stars/ten-star.csv", new String[] {
          "StarID", "ProperName", "X", "Y", "Z"
      });
      assertEquals(stars.size(), 10);
    } catch (IOException e) {
      System.out.println("ERROR: There was a problem reading this file.");
    }
  }

  /**
   * Test to ensure that all 119617 stars are added to the ArrayList by the
   * Parser.
   */
  @Test
  public void testAllStarsRead() {
    StarsParser reader = new StarsParser();
    ArrayList<Star> stars;
    try {
      stars = reader.readCSV("data/stars/stardata.csv", new String[] {
          "StarID", "ProperName", "X", "Y", "Z"
      });
      assertEquals(stars.size(), 119617);
    } catch (IOException e) {
      System.out.println("ERROR: There was a problem reading this file.");
    }
  }
}
