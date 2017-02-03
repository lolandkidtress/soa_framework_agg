package com.James.avroNettyClientConnect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
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

  private int minConnections ; // 空闲池，最小连接数
  private int maxConnections ; // 空闲池，最大连接数
  private int initConnections ;// 初始化连接数
  private int highWaterMark ; //可用连接少于10%则自动扩容
  private int extendPercent ; //数量不够时扩容当前连接的百分比
  //ms
  private int lazyCheck ;// 延迟1分钟后开始检查
  private int periodCheck ;// 检查频率 30秒检查一次
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

  //拥有定时监控的线程池
  private ScheduledExecutorService executorService;
  //用于扩容和收缩的线程池
  private ExecutorService mainExecutorService = Executors.newFixedThreadPool(1);

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
    logger.info(
        "avroConnectionPool初始化结束:");
  }




  private void maintenanceConnectionPool(){
    int size = registerConnections.size();
    logger.info("avroConnectionPool维护进程:" + "注册:" + size + ",可用:" + freeConnectionsLQ.size());
    if(isBalancing.get()){
      logger.info("avroConnectionPool正在重整");
      return;
    }

    if (size < minConnections) {
      int sizeToBeAdded = minConnections - size;
      logger.info("可用数少于最小配置");
      addAvroNettyClientConnection(sizeToBeAdded);

    } else if (size > minConnections) {
      int sizeToBeRemoved = size - minConnections;
      logger.info("收缩到最少配置");
      removeAvroNettyClientConnection(sizeToBeRemoved);

    }
  }

  public void initAvroNettyClientConnectionPool(){
    if(registerConnections.size()<this.initConnections){
      addAvroNettyClientConnection(this.initConnections-registerConnections.size());
    }
    this.pools_count.set(this.initConnections);

  }

  private void addAvroNettyClientConnection(int num){

    if(this.isBalancing.get()){
      //维护中
      return;
    }
    synchronized (isBalancing){
      isBalancing.compareAndSet(false,true);
      //TODO 判断是否超过max
      for(int i=0;i<num;i++){
        avroNettyClientConnection conn = new avroNettyClientConnection(this.host,this.port);
        registerConnections.put(conn.getName(),conn);
        freeConnectionsLQ.add(conn);
        this.pools_count.incrementAndGet();
      }
      isBalancing.compareAndSet(true,false);
    }
    logger.info(
        "avroConnectionPool扩容结束:" + "注册:" + this.registerConnections.size() + ",可用:" + this.freeConnectionsLQ.size());

  }

  //收缩
  private void removeAvroNettyClientConnection(int num){
    if(this.isBalancing.get()){
      return;
    }
    synchronized (isBalancing){
      isBalancing.compareAndSet(false, true);
      for(int i=0;i<num;i++){
        this.pools_count.decrementAndGet();
        avroNettyClientConnection conn =freeConnectionsLQ.poll();
        registerConnections.remove(conn.getName());
        conn.destroyConn();
      }
      isBalancing.compareAndSet(true,false);
    }
    logger.info("avroConnectionPool收缩结束:" + "注册:" + this.registerConnections.size() + ",可用:" + this.freeConnectionsLQ.size());


  }

  public avroNettyClientConnection getConnect(){

    if(freeConnectionsLQ.size()<=0) {
      logger.info("没有可以使用的连接");
      addAvroNettyClientConnection(1);
      //调用初始化
      mainExecutorService.submit(new Runnable() {
        @Override
        public void run() {
          if(isBalancing.get()){
            //维护中
            return;
          }
          initAvroNettyClientConnectionPool();
        }
      });
    }

    avroNettyClientConnection conn = freeConnectionsLQ.poll();
    this.blockedConnections.put(conn.getName(),conn);

    if(this.blockedConnections.size() > (this.registerConnections.size() - this.registerConnections.size()*highWaterMark/100)){
      //异步扩容
      logger.info("占用:" + this.blockedConnections.size()+",剩余"+ (this.registerConnections.size() - this.blockedConnections.size()) +",不足"+highWaterMark+"%");

      mainExecutorService.submit(new Runnable() {
        @Override
        public void run() {
          if(isBalancing.get()){
            //维护中
            return;
          }
          int sizeToAdd = registerConnections.size() * extendPercent/100;
          logger.info("自动扩容" + sizeToAdd);

          addAvroNettyClientConnection(sizeToAdd);
        }
      });
    }
    return conn;
  }

  //释放调用完的连接
  //如果没有在reg中找到,说明已经触发过收缩,直接摧毁
  public void releaseConnect(avroNettyClientConnection conn){
    blockedConnections.remove(conn.getName());
    if(registerConnections.containsKey(conn.getName())) {
      freeConnectionsLQ.add(conn);
    }else{
      conn.destroyConn();
    }
  }

  public Map getConnSize(){
    Map size = new HashMap<>();
    size.put("currentAvail",this.freeConnectionsLQ);
    size.put("total",this.registerConnections.size());
    return size;
  }

}
