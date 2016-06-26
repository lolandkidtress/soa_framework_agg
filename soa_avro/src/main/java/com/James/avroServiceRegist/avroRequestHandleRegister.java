package com.James.avroServiceRegist;

import java.util.concurrent.ConcurrentHashMap;

import com.James.avroProto.avrpRequestProto;


/**
 * Created by James on 16/6/26.
 */
public enum avroRequestHandleRegister {
  INSTANCE;

  //记录请求对应的方法
  private ConcurrentHashMap<String,avrpRequestProto> registerServers = new ConcurrentHashMap();

  public avrpRequestProto getRequestHandle(String key){
    return registerServers.get(key);
  }

  public void addRequestHandle(String key, avrpRequestProto clazz){
    registerServers.put(key, clazz);
  }

  public void removeRequestHandle(String key){
    registerServers.remove(key);
  }
}
