package com.github.jonathansavas.parabond.paraworker.java;

import java.util.Properties;

public class ParaWorkerUtil {

  /**
   * Gets an integer property from the system
   * @param property Property name
   * @param defaultProp Default property value
   * @return Property value
   */
  public static int getIntPropOrElse(String property, int defaultProp) {
    String prop = System.getProperty(property);
    return prop == null ? defaultProp : Integer.parseInt(prop);
  }

  /**
   * Gets an integer property from the specified properties object
   * @param props Properties object
   * @param property Property name
   * @param defaultProp Default property value
   * @return Property value
   */
  public static int getIntPropOrElse(Properties props, String property, int defaultProp) {
    String prop = props.getProperty(property);
    return prop == null ? defaultProp : Integer.parseInt(prop);
  }

  /**
   * Gets a String property from the system
   * @param property Property name
   * @param defaultProp Default property value
   * @return Property value
   */
  public static String getStringPropOrElse(String property, String defaultProp) {
    String prop = System.getProperty(property);
    return prop == null ? defaultProp : prop;
  }
}
