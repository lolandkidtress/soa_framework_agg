package com.James.avroNettyCofig;

/**
 * Created by James on 16/6/26.
 */
public class avroConfig {
  private static int DEFAULT_PORT = 65111;

  public static int getDEFAULT_PORT() {
    return DEFAULT_PORT;
  }

  public static void setDEFAULT_PORT(int DEFAULT_PORT) {
    avroConfig.DEFAULT_PORT = DEFAULT_PORT;
  }
}
