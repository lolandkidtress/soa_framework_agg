package com.James.Invoker;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.zkTools.zkClientTools;


/**
 * Created by James on 16/6/2.
 * 服务调用辅助
 */
public enum InvokerHelper {

  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(InvokerHelper.class.getName());

  private zkClientTools zkclient;

  //关注的服务提供者
  private ConcurrentHashMap<String,Invoker> watchedInvokers = new ConcurrentHashMap();

  public Invoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public void setWatchedInvokers(String key,Invoker invoker){
    this.watchedInvokers.put(key,invoker);
  }



}
