package com.github.jonathansavas.parabond.paradispatcher.java;

public class ParaDispatcherUtil {

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
   * Gets a String property from the system
   * @param property Property name
   * @param defaultProp Default property value
   * @return Property value
   */
  public static String getStringPropOrElse(String property, String defaultProp) {
    String prop = System.getProperty(property);
    return prop == null ? defaultProp : prop;
  }

  public static int getPortOrElse(int defaultPort) {
    String port = System.getProperty("port");
    return port == null ? defaultPort : Integer.parseInt(port);
  }

  /**
   * Get String value from environment variable.
   * @param env Name of environment variable
   * @param defaultEnv Default value
   * @return Variable value
   */
  public static String getStringEnvOrElse(String env, String defaultEnv) {
    String var = System.getenv(env);
    return var == null || var.isEmpty() ? defaultEnv : var;
  }

  /**
   * Get int value from environment variable.
   * @param env Name of environment variable
   * @param defaultEnv Default value
   * @return Variable value
   */
  public static int getIntEnvOrElse(String env, int defaultEnv) {
    String var = System.getenv(env);
    return var == null || var.isEmpty() ? defaultEnv : Integer.parseInt(var);
  }
}
