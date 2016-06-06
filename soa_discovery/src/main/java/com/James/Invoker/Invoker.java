package com.James.Invoker;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.HashFunction.IHashFunction;
import com.James.Listeners.dataChangedListener;
import com.James.Model.SharedProvider;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.zkTools.zkChildChangedListener;
import com.James.zkTools.zkClientTools;
import com.James.zkTools.zkConnectionStateListener;
import com.James.zkTools.zkDataChangedListener;


/**
 * Created by James on 16/6/2.
 */
public class Invoker {

  private static final Logger LOGGER = LoggerFactory.getLogger(Invoker.class.getName());

  public IHashFunction algo = IHashFunction.MURMUR_HASH;

  //一致性hash环
  public TreeMap<Long, SharedProvider> TreeMapNodes = new TreeMap<>();

  public TreeMap<Long, SharedProvider> getTreeMap(){
    return this.TreeMapNodes;
  }

  //数字越大,虚拟节点越多,分布越平均
  public static final int basic_virtual_node_number = 40;

  private zkClientTools zkclient;


  public Invoker(String server_name,String zkconnect) {

    if (!zkClientTools.isConnected(zkconnect)) {
      LOGGER.error("zookeeeper连接失败");
    }
    this.zkclient = new zkClientTools(zkconnect, "");
    LOGGER.info("zookeeeper连接成功");

    initInvoker(server_name);
  }

  public Invoker(String server_name,String zkconnect,String providerMangerPath) {

    if (!zkClientTools.isConnected(zkconnect)) {
      LOGGER.error("zookeeeper连接失败");
    }
    this.zkclient = new zkClientTools(zkconnect, providerMangerPath);
    LOGGER.info("zookeeeper连接成功");

    initInvoker(server_name);
  }

  private void initInvoker(String server_name ){

    try{

      if(!this.zkclient.checkExists(CommonConfig.SLASH.concat(server_name))){
        LOGGER.error("没有名称为" + server_name + "的服务提供者");
      }else{
        LOGGER.info("开始扫描" + server_name+"服务提供的数据");

        StringBuilder sb = new StringBuilder();
        sb.append(CommonConfig.SLASH);
        sb.append(server_name);

        //添加watch
        //watch是一次性的,触发后,需要重新添加watch
        watchZKConnectStat(sb.toString());
//        watchZKChildChange(sb.toString());
        watchZKDataChange(sb.toString());

        InvokerHelper.INSTANCE.setWatchedInvokers(CommonConfig.SLASH.concat(server_name),this);

      }


    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("zookeeeper连接异常");
    }

  }

  public static Invoker create(String server_name,String zkconnect){
    return new Invoker(server_name,zkconnect);
  }

  public static Invoker create(String server_name,String zkconnect,String providerMangerPath){
    return new Invoker(server_name,zkconnect,providerMangerPath);
  }

  //在环上获取节点
  public SharedProvider get(String seed) {
    //TODO
    //if ava_node.size=0; remap

    SortedMap<Long, SharedProvider> tail = TreeMapNodes.tailMap(algo.hash(seed.getBytes(CommonConfig.CHARSET)));
    if (tail.isEmpty()) {
      Map.Entry<Long, SharedProvider> firstEntry = TreeMapNodes.firstEntry();
      if (firstEntry != null) {
        return firstEntry.getValue();
      }
      return null;
    }
    return tail.get(tail.firstKey());
  }

  //计算一致性hash的key后加入环中
  public void add(SharedProvider sharedProvider) {
    for (int n = 0; n < basic_virtual_node_number ; n++) {
      try {
        Long key = this.algo.hash(
            new StringBuilder(sharedProvider.getIdentityID())
                .append("*")
                .append(n).toString()
            , CommonConfig.CHARSET);

        TreeMapNodes.put(key, sharedProvider);

      } catch (UnsupportedEncodingException e) {
        LOGGER.error("添加节点失败", e);
      }
    }
  }

  //从环中删除
  public void remove(SharedProvider sharedProvider) {
    for (int n = 0; n < basic_virtual_node_number ; n++) {
      try {
        Long key = this.algo.hash(
            new StringBuilder(sharedProvider.getIdentityID())
                .append("*")
                .append(n).toString()
            , CommonConfig.CHARSET);

        TreeMapNodes.remove(key);
      } catch (UnsupportedEncodingException e) {
        LOGGER.error("删除节点失败", e);
      }
    }
  }

  //***********************************************************//
  //监听器
  //保存数据变更后的触发事件列表
  private ConcurrentHashMap<String,zkConnectionStateListener> InvokerConnectionStateListeners = new ConcurrentHashMap();
  private ConcurrentHashMap<String,zkDataChangedListener> InvokerDataChangedListeners = new ConcurrentHashMap();
  private ConcurrentHashMap<String,zkChildChangedListener> InvokerzkChildChangedListeners = new ConcurrentHashMap();

  public void watchZKDataChange(String watchPath) {

    CuratorFramework zktools = zkclient.getCuratorFramework();

    //不能每次都新建lsrn,会有重复事件发生
    zkDataChangedListener DataChangedListener = this.InvokerDataChangedListeners.get(watchPath);

    if(DataChangedListener==null){
      DataChangedListener = new zkDataChangedListener(watchPath,new dataChangedListener());
      this.InvokerDataChangedListeners.put(watchPath,DataChangedListener);
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
    zkChildChangedListener ChildChangedListener = this.InvokerzkChildChangedListeners.get(watchPath);

    if(ChildChangedListener==null){
      ChildChangedListener = new zkChildChangedListener(watchPath,new dataChangedListener());
      this.InvokerzkChildChangedListeners.put(watchPath,ChildChangedListener);
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
    zkConnectionStateListener ConnectionStateListener = this.InvokerConnectionStateListeners.get(watchPath);

    if(ConnectionStateListener==null){
      ConnectionStateListener = new zkConnectionStateListener(watchPath,new dataChangedListener());
      this.InvokerConnectionStateListeners.put(watchPath,ConnectionStateListener);
    }
    //watch ZK
    try {
      LOGGER.info("watch " + watchPath + " ConnectStat");
      zkclient.watchConnectStat(zktools, watchPath, ConnectionStateListener);

    } catch (Exception e) {

    }

  }

}
