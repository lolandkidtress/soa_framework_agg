package com.James;

import com.James.basic.UtilsTools.CommonConfig;


/**
 * Created by James on 2017/2/4.
 */
public enum  MonitorInstance {
  INSTANCE;

  //服务标记
  //以self启动时,只记录对应clientID的记录消息
  //以server启动时,记录所有监控记录
  private final static String clientID= CommonConfig.clientID;

  MonitorInstance(){

  }

  private void startTrackingSelf(){

  }

  private void startTrackingAsServer(){

  }

  public String getClientID() {
    return clientID;
  }
}
