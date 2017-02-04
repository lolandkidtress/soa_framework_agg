package com.James.Model;

/**
 * Created by James on 2017/2/4.
 */
public class trackingChain {

  //RPC调用时,自动带有trackingID
  private String trackingID;
  //
  private String clientID;
  //class name+method name
  private String className;

  private String methodName;
  //如果有的话,requestMapping的name或者avro的Name
  private String requestName;
  //开始时间
  private long start_time;
  //结束时间
  private long end_time;
}
