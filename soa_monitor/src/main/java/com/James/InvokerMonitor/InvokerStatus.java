package com.James.InvokerMonitor;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.James.basic.Invoker.Invoker;
import com.James.basic.Model.SharedNode;


/**
 * Created by James on 16/6/2.
 * 服务调用辅助
 * 记录节点在zk上的信息和权重,以及调用的次数
 */
public class InvokerStatus {


  public InvokerStatus(){

  }

  //关注的服务提供者实体
  private static ConcurrentHashMap<String, Invoker> watchedInvokers = new ConcurrentHashMap();

  //服务名和服务节点信息映射
  private static ConcurrentHashMap<String,HashSet<SharedNode>> watchedProviders = new ConcurrentHashMap();

  //
  public static void addWatchedProvider(SharedNode SharedNode){

      HashSet<com.James.basic.Model.SharedNode> ps = watchedProviders.getOrDefault(SharedNode.getServer_name(),new HashSet<>());
      ps.add(SharedNode);
      watchedProviders.put(SharedNode.getServer_name(), ps);
  }

  public static ConcurrentHashMap<String, Invoker> getWatchedInvokers(){
    return watchedInvokers;
  }

  public static Invoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public static void setWatchedInvokers(String key, Invoker remoteInvoker){
    watchedInvokers.put(key, remoteInvoker);
  }


  public static ConcurrentHashMap<String, HashSet<SharedNode>> getWatchedProvider() {
    return watchedProviders;
  }

  public static void setWatchedProvider(ConcurrentHashMap<String, HashSet<SharedNode>> watchedProvider) {
    watchedProviders = watchedProvider;
  }
}
