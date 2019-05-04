package edu.brown.cs.jkjk.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reads in the data regarding current courses, buildings and departments.
 *
 * @author kvlynch
 */
public class DataReader {

  private List<String> departments;
  private Map<String, Set<String>> courses;
  private List<String> buildings;
  private Map<String, double[]> buildingsLocation;

  private static final String COMMA_DELIMITER = ",";

  public DataReader() {
    departments = new ArrayList<>();
    courses = new HashMap<>();
    buildings = new ArrayList<>();
    buildingsLocation = new HashMap<>();
  }

  /**
   * Reads and stores list of departments from the given file path.
   *
   * @param deptFilepath the path to the file containing the departments
   * @return a list of departments
   */
  public List<String> departments(String deptFilepath) {
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(new FileReader(deptFilepath));
      String line = null;

      while ((line = reader.readLine()) != null) {
        String[] data = line.split(COMMA_DELIMITER);
        departments.add(data[0]);
      }

    } catch (IOException e) {
      System.out.println("ERROR: the filepath could not be opened");
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ie) {
        System.out.println("ERROR: Couldn't close buffer reader.");
      }
    }
    return departments;
  }

  /**
   * Reads and stores list of courses from the given file path.
   *
   * @param courseFilepath the path to the file containing course info
   * @return a map of departments to courses
   */
  public Map<String, Set<String>> courses(String courseFilepath) {
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(new FileReader(courseFilepath));

      //Read out headers
      reader.readLine();
      String line;

      while ((line = reader.readLine()) != null) {
        String[] data = line.split(COMMA_DELIMITER);
        if (courses.containsKey(data[0])) {
          Set<String> deptCourses = courses.get(data[0]);
          deptCourses.add(data[1]);
          courses.replace(data[0], deptCourses);
        } else {
          Set<String> deptCourses = new HashSet<>();
          deptCourses.add(data[1]);
          courses.put(data[0], deptCourses);
        }
      }
    } catch (IOException e) {
      System.out.println("ERROR: the filepath could not be opened");
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ie) {
        System.out.println("ERROR: Couldn't close buffer reader.");
      }
    }

    return courses;

  }

  /**
   * Reads and stores list of buildings from given file path.
   *
   * @param buildingsFilepath the path to the file containing building info
   * @return a list of buildings
   */
  public List<String> buildings(String buildingsFilepath) {
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(new FileReader(buildingsFilepath));
      String line;

      //get rid of headers
      reader.readLine();

      while ((line = reader.readLine()) != null) {
        String[] data = line.split(COMMA_DELIMITER);
        buildings.add(data[0]);
        double lat = Double.parseDouble(data[1]);
        double lon = Double.parseDouble(data[2]);
        double[] position = new double[2];
        position[0] = lat;
        position[1] = lon;
        buildingsLocation.put(data[0], position);


      }

    } catch (IOException e) {
      System.out.println("ERROR: the building filepath could not be opened.");
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ie) {
        System.out.println("ERROR: Couldn't close buffer reader.");
      }
    }
    return buildings;
  }

  /**
   * Returns the map of building names to positions
   *
   * @return building locations
   */
  public Map<String, double[]> getBuildingsLocation() {
    return buildingsLocation;
  }

}
