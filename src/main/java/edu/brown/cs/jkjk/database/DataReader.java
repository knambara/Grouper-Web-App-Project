package edu.brown.cs.jkjk.database;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;


public class DataReader {

  private List<String> departments;
  private Map<String, Set<String>> courses;

  private static final String COMMA_DELIMITER = ",";

  public DataReader() {
    departments = new ArrayList<>();
    courses = new HashMap<>();
  }

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

  public Map<String, Set<String>> courses(String courseFilepath) {
    BufferedReader reader = null;

    try {
      reader = new BufferedReader(new FileReader(courseFilepath));
      String line = null;

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





}
