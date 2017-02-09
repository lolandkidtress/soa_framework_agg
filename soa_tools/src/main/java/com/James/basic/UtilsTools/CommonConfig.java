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

  //默认的提供服务的http端口
  public final static String defaultHttpPort = "9090";
  //默认的提供服务的netty端口
  public final static String defaultAvroPort = "46111";
  //监控用端口
  public final static String nanoHttpPort = "9091";

  //常量
  public final static String s_trackingData = "trackingData";
  public final static String s_trackingID = "trackingID";
  public final static String s_sequence = "Sequence";
  public final static String s_invokerID = "invokerID1";
  public final static String s_clientID = "clientID";
  //用于标记自身
  //TODO 生成算法->服务名+ip+端口
  public final static String clientID= Utils.generateClientID();



//  public enum SUBMIT_MODE {
//    get,post,put,delete
//  }
  //http请求方式
  public enum RequestMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
  }

  //支持的协议
  public enum PROTOCOL {
    http,thrift,avro,protoc
  }

  //zk事件
  public enum zkEventType{
    DataChanged,ConnectionRecover,ConnectionLost,NodeChildrenChanged,None
  }
}
