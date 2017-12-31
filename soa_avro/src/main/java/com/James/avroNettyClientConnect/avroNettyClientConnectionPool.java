package com.James.avroNettyClientConnect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Exception.avroConnectionException;
import com.James.basic.Enum.BasicCode;


/**
 * Created by James on 2017/1/22.
 */
public class avroNettyClientConnectionPool {

  private static final Log logger= LogFactory.getLog(avroNettyClientConnectionPool.class.getName());

  private String host;  //ip
  private int port;    //端口
  private boolean keepAlive; //保持

  private int minConnections ; // 最小连接数
  private int maxConnections ; // 最大连接数
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
  private LinkedBlockingQueue<avroNettyClientConnection> freeConnectionsLQ = new LinkedBlockingQueue();
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
        try{
          maintenanceConnectionPool();
        }catch(Exception e){
          e.printStackTrace();
          logger.error("维护连接池异常");
        }

      }
    }, lazyCheck, periodCheck, TimeUnit.MILLISECONDS);
    //初始化线程池
    initAvroNettyClientConnectionPool();
    logger.info(
        "avroConnectionPool初始化结束:");
  }

  private void maintenanceConnectionPool() throws Exception{

    int size = freeConnectionsLQ.size();
    logger.info("avroConnectionPool维护进程:" + "注册:" + size + ",可用:" + freeConnectionsLQ.size());
    if(isBalancing.get()){
      logger.info("avroConnectionPool正在重整");
      return;
    }

    if (size < minConnections) {
      int sizeToBeAdded = minConnections - size;
      logger.info("可用数少于最小配置");
      try{
        addAvroNettyClientConnection(sizeToBeAdded);
      }catch(avroConnectionException e){
        logger.error(
            "avroConnectionPool扩容异常,已扩容到最大配置");
      }


    } else if (size > minConnections) {
      int sizeToBeRemoved = size - minConnections;
      logger.info("收缩到最少配置");
      removeAvroNettyClientConnection(sizeToBeRemoved);

    }
  }

  public void initAvroNettyClientConnectionPool() {
    if(registerConnections.size()<this.initConnections){
      try{
        addAvroNettyClientConnection(this.initConnections-registerConnections.size());
      }catch(avroConnectionException e){
        logger.error(
            "avroConnectionPool扩容异常,已扩容到最大配置");
      }

    }
    this.pools_count.set(this.initConnections);

  }

  private void addAvroNettyClientConnection(int num) throws avroConnectionException{

    if(this.isBalancing.get()){
      //维护中
      return;
    }
    if(registerConnections.size()>maxConnections){
      throw new avroConnectionException(BasicCode.avro_Connection_Max_limit.code, BasicCode.avro_Connection_Max_limit.note);
    }
    synchronized (isBalancing){
      isBalancing.compareAndSet(false,true);
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
  private void removeAvroNettyClientConnection(int num) throws Exception{
    if(this.isBalancing.get()){
      return;
    }
    synchronized (isBalancing){
      isBalancing.compareAndSet(false, true);
      for(int i=0;i<num;i++){
        this.pools_count.decrementAndGet();
        avroNettyClientConnection conn =freeConnectionsLQ.poll(1000,TimeUnit.MILLISECONDS);
        registerConnections.remove(conn.getName());
        conn.destroyConn();
      }
      isBalancing.compareAndSet(true,false);
    }
    logger.info("avroConnectionPool收缩结束:" + "注册:" + this.registerConnections.size() + ",可用:" + this.freeConnectionsLQ.size());


  }

  public avroNettyClientConnection getConnect() throws avroConnectionException{

    if(freeConnectionsLQ.size()<=0) {
      try{
        addAvroNettyClientConnection(1);
      }catch(avroConnectionException e){
        logger.error(
            "avroConnectionPool扩容异常,已扩容到最大配置");
        throw new avroConnectionException(BasicCode.avro_Connection_Max_limit.code, BasicCode.avro_Connection_Max_limit.note);
      }
      //调用初始化
      mainExecutorService.submit(new Runnable() {
        @Override
        public void run() {
          if (isBalancing.get()) {
            //维护中
            return;
          }
          initAvroNettyClientConnectionPool();
        }
      });
//      throw new avroConnectionException(BasicCode.avro_Connection_not_available.code,BasicCode.avro_Connection_not_available.note);
    }
    try{
      //等待1秒
      avroNettyClientConnection conn = freeConnectionsLQ.poll(1000, TimeUnit.MILLISECONDS);
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
            try{
              addAvroNettyClientConnection(sizeToAdd);
            }catch(avroConnectionException e){
              logger.error(
                  "avroConnectionPool扩容异常,已扩容到最大配置");
            }

          }
        });
      }

      return conn;
    }catch(Exception e){
      e.printStackTrace();
      throw new avroConnectionException(BasicCode.avro_Connection_not_available.code, BasicCode.avro_Connection_not_available.note);
    }

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
    size.put("currentAvail",this.freeConnectionsLQ.size());
    size.put("total",this.registerConnections.size());
    return size;
  }

}
