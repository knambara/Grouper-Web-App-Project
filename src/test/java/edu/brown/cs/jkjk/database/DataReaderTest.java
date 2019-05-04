package edu.brown.cs.jkjk.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class DataReaderTest {

  @Test
  public void departmentsTest() {
    DataReader dr = new DataReader();
    List<String> departments = dr.departments("data/departments_sample.csv");
    assertEquals(departments.size(), 19);
    assertTrue(departments.contains("Computer Science"));
    assertFalse(departments.contains("Visual Art"));
  }

  @Test
  public void coursesTest() {
    DataReader dr = new DataReader();
    Map<String, Set<String>> courseMap = dr.courses("data/sample_courses.csv");
    System.out.println(courseMap);
    /*
     * assertEquals(courseMap.size(), 7); assertEquals(courseMap.get("Computer Science").size(), 5);
     * assertEquals(courseMap.get("Economics").size(), 5);
     * assertEquals(courseMap.get("Political Science").size(), 4);
     * assertEquals(courseMap.get("Physics ").size(), 3);
     * assertEquals(courseMap.get("Mathematics").size(), 5);
     * assertEquals(courseMap.get("Applied Mathematics").size(), 3);
     * assertEquals(courseMap.get("Biology").size(), 3);
     */
  }

  @Test
  public void buildingsTest() {
    DataReader dr = new DataReader();
    List<String> buildings = dr.buildings("data/buildings_sample.csv");
    assertEquals(buildings.size(), 26);
    assertTrue(buildings.contains("Barus Building"));
    assertFalse(buildings.contains("Hegeman Hall"));
  }
}
