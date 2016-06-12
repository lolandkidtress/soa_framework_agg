package com.James.avroServerHandle;

import java.util.concurrent.ConcurrentHashMap;

import com.James.avroProto.avrpRequestProto;


/**
 * Created by James on 16/6/8.
 */
public enum avroServerHandle {

  INSTANCE;

  //记录请求对应的方法
  private ConcurrentHashMap<String,avrpRequestProto> registerServers = new ConcurrentHashMap();

  public avrpRequestProto getRegisterServers(String key){
      return registerServers.get(key);
  }

  public void addRegisterServers(String key, avrpRequestProto clazz){
    registerServers.put(key, clazz);
  }

  public void removeRegisterServers(String key){
    registerServers.remove(key);
  }
}
