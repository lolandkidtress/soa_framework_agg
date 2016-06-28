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
  public final static String COLON = ":";
  public final static String DEFAULTVERSION = "defaultVersion";

  public final static String HTTP_PROTOCOL_PREFIX = "http://";

  public final static String defaultHttpPort = "9090";
  public final static String defaultAvroPort = "46111";



//  public enum SUBMIT_MODE {
//    get,post,put,delete
//  }

  public enum RequestMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
  }

  public enum PROTOCOL {
    http,thrift,avro,protoc
  }

  public enum zkEventType{
    DataChanged,ConnectionRecover,ConnectionLost,NodeChildrenChanged,None
  }
}
