package com.James.Invoker;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Listeners.iListeners;
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

  //监听器
  //保存数据变更后的触发事件列表
  private ConcurrentHashMap<String,iListeners> InvokerListeners = new ConcurrentHashMap();

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

  public void watchZKConfigDataChange(String watchPath ,iListeners Lsrner) {

    CuratorFramework zktools = zkclient.getCuratorFramework();

    zkDataChangedListener DataChangedListener = new zkDataChangedListener(watchPath,Lsrner);
    //watch ZK
    try {
      LOGGER.info("watch " + watchPath + " DataChanged");
      zkclient.watchedData(zktools, watchPath, DataChangedListener);

    } catch (Exception e) {

    }

  }

  public void watchZKConfigChildChange(String watchPath ,iListeners Lsrner) {

    CuratorFramework zktools = zkclient.getCuratorFramework();

    zkChildChangedListener ChildChangedListener = new zkChildChangedListener(watchPath,Lsrner);
    //watch ZK
    try {
      LOGGER.info("watch " + watchPath + " ChildChanged");
      zkclient.watchedChildChanged(zktools, watchPath, ChildChangedListener);

    } catch (Exception e) {

    }

  }


  public void watchZKConfigConnectStat(String watchPath ,iListeners Lsrner) {

    CuratorFramework zktools = zkclient.getCuratorFramework();

    zkConnectionStateListener ConnectionStateListener = new zkConnectionStateListener(watchPath,Lsrner);
    //watch ZK
    try {
      LOGGER.info("watch " + watchPath + " ConnectStat");
          zkclient.watchConnectStat(zktools, watchPath, ConnectionStateListener);

    } catch (Exception e) {

    }

  }


}
