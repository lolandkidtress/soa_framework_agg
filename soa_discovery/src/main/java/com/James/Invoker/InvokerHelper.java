package com.James.Invoker;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.James.Model.sharedNode;


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
  private ConcurrentHashMap<String,Invoker> watchedInvokers = new ConcurrentHashMap();

  //服务名和服务节点信息映射
  private ConcurrentHashMap<String,HashSet<sharedNode>> watchedProviders = new ConcurrentHashMap();

  //
  public void addWatchedProvider(sharedNode SharedNode){

      HashSet<sharedNode> ps = watchedProviders.getOrDefault(SharedNode.getServer_name(),new HashSet<>());
      ps.add(SharedNode);
      watchedProviders.put(SharedNode.getServer_name(), ps);
  }

  public ConcurrentHashMap<String,Invoker> getWatchedInvokers(){
    return watchedInvokers;
  }

  public Invoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public void setWatchedInvokers(String key,Invoker invoker){
    this.watchedInvokers.put(key,invoker);
  }


  public ConcurrentHashMap<String, HashSet<sharedNode>> getWatchedProvider() {
    return watchedProviders;
  }

  public void setWatchedProvider(ConcurrentHashMap<String, HashSet<sharedNode>> watchedProvider) {
    this.watchedProviders = watchedProvider;
  }
}
