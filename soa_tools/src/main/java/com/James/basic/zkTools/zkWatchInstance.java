package com.James.basic.zkTools;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;


/**
 * Created by James on 16/8/9.
 */
public class zkWatchInstance {

  private static final Log LOGGER = LogFactory.getLog(zkWatchInstance.class.getName());

  private ConcurrentHashMap<String,zkWatchImpl> watchers = new ConcurrentHashMap();

  public enum ZookeeperWatcherType{
    GET_DATA,GET_CHILDREN,EXITS,CREATE_ON_NO_EXITS
  }

  private static class InnerInstance {
    public static final zkWatchInstance instance = new zkWatchInstance();
  }

  public static zkWatchInstance getInstance() {
    return InnerInstance.instance;
  }


  public zkWatchInstance() {
  }


  public void addDataWatch(String path,CuratorFramework zkTools,iListeners lsrn) throws Exception{
    zkWatchImpl watch = new zkWatchImpl(path,zkTools,lsrn);
    zkTools.getData().
        usingWatcher(watch).forPath(path);

    LOGGER.info(path+":添加数据变更watch.");
  }

  public void addChildWatch(String path,CuratorFramework zkTools,iListeners lsrn) throws Exception{
    zkWatchImpl watch = new zkWatchImpl(path,zkTools,lsrn);
    zkTools.getChildren().
        usingWatcher(watch).forPath(path);
    LOGGER.info(path + ":添加子节点变更watch.");
  }


  public void addConnectWatch(String path,CuratorFramework zkTools,iListeners lsrn){

        LOGGER.info(path+":添加连接状态变更Listener.");

        zkTools.getConnectionStateListenable().addListener(new ConnectionStateListener() {
          @Override
          public void stateChanged(CuratorFramework client, ConnectionState newState) {
            LOGGER.info("连接状态"+newState);

            if(newState == ConnectionState.RECONNECTED){
              //TODO 全部重新watch
              lsrn.Handle(path, newState.toString());
            }
          }
        });
  }


  //添加数据变动,子节点变动,连接状态变动
  public void watch(String path,CuratorFramework zkTools,iListeners lsrn) throws Exception{

    addDataWatch(path, zkTools,lsrn);
    addChildWatch(path,zkTools,lsrn);
    addConnectWatch(path,zkTools,lsrn);

  }


  public static void main(String[] args) throws Exception {

    CuratorFramework zkTools = CuratorFrameworkFactory
        .builder()
        .connectString("127.0.01:2181")
        .namespace("")
        .retryPolicy(new RetryNTimes(5,2000))
        .build();
    zkTools.start();

    zkWatchInstance test = zkWatchInstance.getInstance();
    String path="/watch_test";

    test.watch(path,zkTools, new iListeners(){
      @Override
      public void Handle(String path, String type) {
        LOGGER.info("接收到"+ path +"事件:" + type);

      }
    });

//    test.register();
//    test.put();
    Thread.sleep(10000000000L);

  }


}
