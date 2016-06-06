package com.James.Invoker;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Listeners.dataChangedListener;
import com.James.Model.providerInvoker;
import com.James.zkInstance;
import com.James.zkTools.zkChildChangedListener;
import com.James.zkTools.zkClientTools;
import com.James.zkTools.zkConnectionStateListener;
import com.James.zkTools.zkDataChangedListener;


/**
 * Created by James on 16/6/2.
 * 服务调用辅助
 */
public enum InvokerHelper {

  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(InvokerHelper.class.getName());

  private zkClientTools zkclient;

  //关注的服务提供者
  private ConcurrentHashMap<String,providerInvoker> watchedInvokers = new ConcurrentHashMap();

//  private zkConnectionStateListener ConnectionStateListener = new zkConnectionStateListener(sb.toString(),lsrner);
//  private zkDataChangedListener DataChangedListener = new zkDataChangedListener(sb.toString(),lsrner);
//  private zkChildChangedListener DataChangedListener = new zkDataChangedListener(sb.toString(),lsrner);

  //监听器
  //保存数据变更后的触发事件列表
  private ConcurrentHashMap<String,zkConnectionStateListener> InvokerConnectionStateListeners = new ConcurrentHashMap();
  private ConcurrentHashMap<String,zkDataChangedListener> InvokerDataChangedListeners = new ConcurrentHashMap();
  private ConcurrentHashMap<String,zkChildChangedListener> InvokerzkChildChangedListeners = new ConcurrentHashMap();



  public providerInvoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public void init(){

    if(this.zkclient==null){
      this.zkclient= zkInstance.INSTANCE.getZkclient();
      if(this.zkclient!=null){
        LOGGER.info("zookeeeper连接成功");
      }else{
        LOGGER.error("zookeeeper连接失败");
      }


    }
  }

  public void watchZKDataChange(String watchPath) {

    CuratorFramework zktools = zkclient.getCuratorFramework();

    //不能每次都新建lsrn,会有重复事件发生
    zkDataChangedListener DataChangedListener = InvokerDataChangedListeners.get(watchPath);

    if(DataChangedListener==null){
      DataChangedListener = new zkDataChangedListener(watchPath,new dataChangedListener());
      InvokerDataChangedListeners.put(watchPath,DataChangedListener);
    }
    //watch ZK
    try {
      LOGGER.info("watch " + watchPath + " DataChanged");
      zkclient.watchedData(zktools, watchPath, DataChangedListener);

    } catch (Exception e) {

    }

  }

  public void watchZKChildChange(String watchPath) {

    CuratorFramework zktools = zkclient.getCuratorFramework();

    //不能每次都新建lsrn,会有重复事件发生
    zkChildChangedListener ChildChangedListener = InvokerzkChildChangedListeners.get(watchPath);

    if(ChildChangedListener==null){
      ChildChangedListener = new zkChildChangedListener(watchPath,new dataChangedListener());
      InvokerzkChildChangedListeners.put(watchPath,ChildChangedListener);
    }

    //watch ZK
    try {
      LOGGER.info("watch " + watchPath + " ChildChanged");
      zkclient.watchedChildChanged(zktools, watchPath, ChildChangedListener);

    } catch (Exception e) {

    }

  }


  public void watchZKConnectStat(String watchPath) {

    CuratorFramework zktools = zkclient.getCuratorFramework();

    //不能每次都新建lsrn,会有重复事件发生
    zkConnectionStateListener ConnectionStateListener = InvokerConnectionStateListeners.get(watchPath);

    if(ConnectionStateListener==null){
      ConnectionStateListener = new zkConnectionStateListener(watchPath,new dataChangedListener());
      InvokerConnectionStateListeners.put(watchPath,ConnectionStateListener);
    }
    //watch ZK
    try {
      LOGGER.info("watch " + watchPath + " ConnectStat");
          zkclient.watchConnectStat(zktools, watchPath, ConnectionStateListener);

    } catch (Exception e) {

    }

  }


}
