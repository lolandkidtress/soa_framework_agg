package com.James.avroServiceRegist;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by James on 16/6/26.
 * 单例
 * 处理方法mapping表
 *
 */
public enum avroRequestHandleRegister {
  INSTANCE;

  //记录请求对应的方法
  private ConcurrentHashMap<String,Class> requestHandles = new ConcurrentHashMap();

  public Class getRequestHandle(String key){
    return requestHandles.get(key);
  }

//  public void addRequestHandle(String key, avrpRequestProto clazz){
//    requestHandles.put(key, clazz);
//  }
    public void addRequestHandle(String key, Class clazz){
      requestHandles.put(key, clazz);
    }

  public void removeRequestHandle(String key){
    requestHandles.remove(key);
  }
}
