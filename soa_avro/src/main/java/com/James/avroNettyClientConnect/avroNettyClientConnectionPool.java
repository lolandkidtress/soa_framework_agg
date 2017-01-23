package com.James.avroNettyClientConnect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Created by James on 2017/1/22.
 */
public class avroNettyClientConnectionPool {

  private static final Log logger= LogFactory.getLog(avroNettyClientConnectionPool.class.getName());

  private String host;  //ip
  private int port;    //端口
  private boolean keepAlive; //保持

  private int minConnections = 5; // 空闲池，最小连接数
  private int maxConnections = 10; // 空闲池，最大连接数
  private int initConnections = 5;// 初始化连接数
  private int highWaterMark = 10; //可用连接少于10%则自动扩容
  private int extendPercent = 10; //数量不够时扩容当前连接的百分比
  //ms
  private int lazyCheck = 1000*60;// 延迟1分钟后开始检查
  private int periodCheck = 1000*30;// 检查频率 30秒检查一次
  //是否在重新分布中
  private AtomicBoolean isBalancing = new AtomicBoolean(false);

  //激活的连接数
  private AtomicInteger pools_count= new AtomicInteger(0);
  //注册的连接
  private ConcurrentHashMap<String,avroNettyClientConnection> registerConnections = new ConcurrentHashMap();
  //空闲的连接
  private ConcurrentLinkedQueue<avroNettyClientConnection> freeConnectionsLQ = new ConcurrentLinkedQueue();
  //使用中的连接
  private ConcurrentHashMap<String,avroNettyClientConnection> blockedConnections = new ConcurrentHashMap();

  private ScheduledExecutorService executorService;

  public avroNettyClientConnectionPool(String host,int port,
                                        int minConnections,int maxConnections,int initConnections,
                                        int highWaterMark, int extendPercent,
                                        int lazyCheck,int periodCheck){

    this.host=host;
    this.port=port;
    this.minConnections = minConnections;
    this.maxConnections = maxConnections;
    this.initConnections = initConnections;
    this.highWaterMark = highWaterMark;
    this.extendPercent = extendPercent;

    this.lazyCheck = lazyCheck;
    this.periodCheck = periodCheck;

    //初始维护线程
    executorService = Executors.newSingleThreadScheduledExecutor();

    executorService.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        maintenanceConnectionPool();
      }
    }, lazyCheck, periodCheck, TimeUnit.MILLISECONDS);

    initAvroNettyClientConnectionPool();
  }

  private void maintenanceConnectionPool(){
    int size = registerConnections.size();
    logger.info("avroConnectionPool维护进程:" + "注册:" + size + "可用:" + freeConnectionsLQ.size());
    if(isBalancing.get()){
      logger.info("avroConnectionPool正在重整");
      return;
    }

    if (size < minConnections) {
      int sizeToBeAdded = minConnections - size;
      logger.info("可用数少于最小配置");
      isBalancing.compareAndSet(false, true);
      for (int i = 0; i < sizeToBeAdded; i++) {
        addAvroNettyClientConnection();
      }
      isBalancing.compareAndSet(true, false);
      logger.info("补充连接结束");
    } else if (size > maxConnections) {
      int sizeToBeRemoved = size - maxConnections;
      for (int i = 0; i < sizeToBeRemoved; i++) {
        removeAvroNettyClientConnection();
      }
    }
  }
  public void initAvroNettyClientConnectionPool(){
    if(this.isBalancing.get()){
      return;
    }
    this.isBalancing.set(true);
    while(registerConnections.size()<this.initConnections){
      addAvroNettyClientConnection();
    }
    this.pools_count.set(this.initConnections);
    this.isBalancing.set(false);
  }

  private void addAvroNettyClientConnection(){
    if(this.isBalancing.get()){
      return;
    }
    avroNettyClientConnection conn = new avroNettyClientConnection(this.host,this.port);
    registerConnections.put(conn.getName(),conn);
    freeConnectionsLQ.add(conn);
    this.pools_count.incrementAndGet();

  }

  //收缩
  private void removeAvroNettyClientConnection(){
    if(this.isBalancing.get()){
      return;
    }
    this.pools_count.decrementAndGet();
    //TODO 销毁多余的连接
  }

  //从可用里面中poll一个,如果是注册过的则返回,否者继续找,直到触发扩展
  //如果没有注册过,说明已经触发过收缩,作为已经失效
  public avroNettyClientConnection getConnect(){

    if(freeConnectionsLQ.size()<=0) {
      logger.error("没有可以使用的连接");
      //TODO 异步重新初始化
      return null;
    }

    while(freeConnectionsLQ.size()>0){
      avroNettyClientConnection conn = freeConnectionsLQ.poll();
      if(registerConnections.containsKey(conn.getName())){
        blockedConnections.put(conn.getName(),conn);
        if(this.blockedConnections.size() > (this.pools_count.get()*highWaterMark/100)){
          //TODO 异步扩容
        }
        return conn;
      }else{
        conn.destroyConn();
      }
    }

    //freeConnectionsLQ中的连接都已失效
    logger.error("没有可以使用的连接");
    //TODO 异步重新初始化
    return null;
  }

  public void releaseConnect(String name){
    blockedConnections.remove(name);
    freeConnectionsLQ.add(registerConnections.get(name));
  }

}
