package com.James.Invoker;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.James.basic.Model.sharedNode;


/**
 * Created by James on 16/6/2.
 * 服务调用辅助
 * 记录节点信息
 */
public class InvokerHelper  {


  private static class InnerInstance {
    public static final InvokerHelper instance = new InvokerHelper();
  }

  public InvokerHelper(){
      getInstance();
  }

  public static InvokerHelper getInstance() {
    return InnerInstance.instance;
  }

  //关注的服务提供者实体
  private ConcurrentHashMap<String, com.James.Invoker.RemoteInvoker> watchedInvokers = new ConcurrentHashMap();

  //服务名和服务节点信息映射
  private ConcurrentHashMap<String,HashSet<sharedNode>> watchedProviders = new ConcurrentHashMap();

  //
  public void addWatchedProvider(sharedNode SharedNode){

      HashSet<sharedNode> ps = watchedProviders.getOrDefault(SharedNode.getServer_name(),new HashSet<>());
      ps.add(SharedNode);
      watchedProviders.put(SharedNode.getServer_name(), ps);
  }

  public ConcurrentHashMap<String, com.James.Invoker.RemoteInvoker> getWatchedInvokers(){
    return watchedInvokers;
  }

  public com.James.Invoker.RemoteInvoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public void setWatchedInvokers(String key, com.James.Invoker.RemoteInvoker remoteInvoker){
    this.watchedInvokers.put(key, remoteInvoker);
  }


  public ConcurrentHashMap<String, HashSet<sharedNode>> getWatchedProvider() {
    return watchedProviders;
  }

  public void setWatchedProvider(ConcurrentHashMap<String, HashSet<sharedNode>> watchedProvider) {
    this.watchedProviders = watchedProvider;
  }
}
