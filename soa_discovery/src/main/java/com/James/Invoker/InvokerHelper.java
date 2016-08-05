package com.James.Invoker;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.James.Model.sharedProvider;


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
  private ConcurrentHashMap<String,HashSet<sharedProvider>> watchedProviders = new ConcurrentHashMap();

  //
  public void addWatchedProvider(List<sharedProvider> sharedProviders){
    sharedProviders.forEach(sharedProvider -> {
      String server_name = sharedProvider.getServer_name();
      HashSet<sharedProvider> ps = watchedProviders.getOrDefault(server_name,new HashSet<>());
      ps.add(sharedProvider);
      watchedProviders.put(server_name,ps);

    });
  }


  public Invoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public void setWatchedInvokers(String key,Invoker invoker){
    this.watchedInvokers.put(key,invoker);
  }

  public ConcurrentHashMap<String, HashSet<sharedProvider>> getWatchedProvider() {
    return watchedProviders;
  }

  public void setWatchedProvider(ConcurrentHashMap<String, HashSet<sharedProvider>> watchedProvider) {
    this.watchedProviders = watchedProvider;
  }
}
