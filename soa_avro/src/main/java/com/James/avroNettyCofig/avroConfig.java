package com.James.avroNettyCofig;

import com.James.basic.UtilsTools.CommonConfig;


/**
 * Created by James on 16/6/26.
 */
public class avroConfig {
  private static int DEFAULT_PORT = Integer.valueOf(CommonConfig.defaultAvroPort);

  public static int getDEFAULT_PORT() {
    return DEFAULT_PORT;
  }

  public static void setDEFAULT_PORT(int DEFAULT_PORT) {
    avroConfig.DEFAULT_PORT = DEFAULT_PORT;
  }
}
