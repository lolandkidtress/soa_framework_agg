package com.James.avroNettyClientConnect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.James.basic.Model.sharedNode;
import com.James.basic.UtilsTools.CommonConfig;


/**
 * Created by James on 2017/1/22.
 */
public class avroNettyClientConnectionManager {

  private int minConnections = 10; // 空闲池，最小连接数
  private int maxConnections = 50; // 空闲池，最大连接数
  private int initConnections = 10;// 初始化连接数
  private int highWaterMark = 10; //可用连接少于10%则自动扩容
  private int extendPercent = 20; //数量不够时扩容当前连接的百分比

  //ms
  private int lazyCheck = 1000*60;// 延迟1分钟后开始检查
  private int periodCheck = 1000*30;// 检查频率 30秒检查一次

  //注册的连接池
  private ConcurrentHashMap<String,avroNettyClientConnectionPool> registerConnectionPools = new ConcurrentHashMap();


  public static avroNettyClientConnectionManager getInstance(){
    return Singtonle.instance;
  }
  private static class Singtonle {
    private static avroNettyClientConnectionManager instance =  new avroNettyClientConnectionManager();
  }


  public void initConnectionPool(sharedNode SharedNode){
    //根据host和port创建连接池
    avroNettyClientConnectionPool connPools=
        new avroNettyClientConnectionPool(SharedNode.getIP(),
                                          Integer.valueOf(SharedNode.getRpc_port()),
                                          this.minConnections,
                                          this.maxConnections,
                                          this.initConnections,
                                          this.highWaterMark,
                                          this.extendPercent,
                                          this.lazyCheck,
                                          this.periodCheck);

    registerConnectionPools.put(SharedNode.getIP().concat("-").concat(SharedNode.getRpc_port()),connPools);
  }


  public avroNettyClientConnectionPool getConnectPool(String host,String port){
    return registerConnectionPools.get(host.concat(CommonConfig.HYPHEN).concat(port));
  }

  public Map<String,String> getConnectPoolSize(String host,String port){
    avroNettyClientConnectionPool cp = registerConnectionPools.get(host.concat(CommonConfig.HYPHEN).concat(port));
    return cp.getConnSize();

  }


  public int getMinConnections() {
    return minConnections;
  }

  public void setMinConnections(int minConnections) {
    this.minConnections = minConnections;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getInitConnections() {
    return initConnections;
  }

  public void setInitConnections(int initConnections) {
    this.initConnections = initConnections;
  }

  public long getLazyCheck() {
    return lazyCheck;
  }

  public void setLazyCheck(int lazyCheck) {
    this.lazyCheck = lazyCheck;
  }

  public long getPeriodCheck() {
    return periodCheck;
  }

  public void setPeriodCheck(int periodCheck) {
    this.periodCheck = periodCheck;
  }
}
