package com.James.basic.UtilsTools;

import com.James.basic.Model.trackingChain;


/**
 * Created by James on 2017/2/4.
 * 在ThreadLocal中保存调用链信息
 */
public class ThreadLocalCache {
  private static final ThreadLocal<trackingChain> callChain = new ThreadLocal<>();

  public static ThreadLocal<trackingChain> getCallchain() {
    return callChain;
  }

  public static void setCallchain(trackingChain callchain0) {
    callChain.set(callchain0);
  }

  private static final ThreadLocal<String> threadLocalTrackingID = new ThreadLocal<>();

  public static ThreadLocal<String> getTrackingID() {
    return threadLocalTrackingID;
  }

  public static void setTrackingID(String trackingID) {
    threadLocalTrackingID.set(trackingID);
  }

}
