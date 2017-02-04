package com.James;

import java.util.UUID;


/**
 * Created by James on 2017/2/4.
 */
public enum  MonitorInstance {
  INSTANCE;

  //用于标记
  //以self启动时,只记录对应clientID的记录消息
  //以server启动时,记录所有监控记录
  private String clientID= UUID.randomUUID().toString().replaceAll("-", "");

  MonitorInstance(){

  }

  private void startTrackingSelf(){

  }

  private void startTrackingAsServer(){

  }
}
