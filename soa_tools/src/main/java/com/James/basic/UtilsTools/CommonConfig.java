package com.James.basic.UtilsTools;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;


/**
 * Created by James on 16/5/30.
 */
public class CommonConfig {
  public final static ZoneId ZONDID = ZoneId.of("GMT+08:00");
  public final static Charset CHARSET = StandardCharsets.UTF_8;
  public final static String SLASH = "/";
  public final static String UNDERLINE = "_";
  public final static String HYPHEN = "-";


  public enum SUBMIT_MODE {
    get,post,put,delete
  }

  public enum PROTOCOL {
    http,thrift,avro,protoc
  }
}
