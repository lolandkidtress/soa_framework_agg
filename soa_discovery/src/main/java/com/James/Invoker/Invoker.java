package com.James.Invoker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Listeners.dataChangedListener;
import com.James.Model.SharedProvider;
import com.James.Model.providerInvoker;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.zkTools.zkChildChangedListener;
import com.James.zkTools.zkClientTools;
import com.James.zkTools.zkConnectionStateListener;
import com.James.zkTools.zkDataChangedListener;


/**
 * Created by James on 16/6/2.
 */
public class Invoker {

  private static final Logger LOGGER = LoggerFactory.getLogger(Invoker.class.getName());

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
        LOGGER.info("开始扫描" + server_name + "服务提供的数据");

        StringBuilder sb = new StringBuilder();
        sb.append(CommonConfig.SLASH);
        sb.append(server_name);

        //创建环
        buildNodeGroup(sb.toString());
        //添加watch
        //watch是一次性的,触发后,需要重新添加watch
        watchZKConnectStat(sb.toString());
//        watchZKChildChange(sb.toString());
        watchZKDataChange(sb.toString());

        InvokerHelper.INSTANCE.setWatchedInvokers(CommonConfig.SLASH.concat(server_name), this);

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
      e.printStackTrace();
      LOGGER.error("创建watch异常",e);
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
      e.printStackTrace();
      LOGGER.error("创建watch异常",e);
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
      e.printStackTrace();
      LOGGER.error("创建watch异常",e);
   }

  }

  //************************************//


  //key:version,value:providerInvoker
  private ConcurrentHashMap<String,providerInvoker> versionedProviderInvokers= new ConcurrentHashMap();

  //查找版本下的方法
  private void buildNodeGroup(String path){
    try{
      List<String> versions =  zkclient.getChildren(path);

      for(String version : versions){
        LOGGER.info("取得版本" + version );
        providerInvoker ProviderInvoker = buildMethodGroup(path.concat(CommonConfig.SLASH).concat(version));
        versionedProviderInvokers.put(version, ProviderInvoker);
      }
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("初始化可用节点异常",e);
   }

  }

  //方法下的节点做一致性hash
  private providerInvoker buildMethodGroup(String path){
    try{
      List<String> methods =  zkclient.getChildren(path);

      providerInvoker ProviderInvoker=new providerInvoker();

      for(String method : methods){

        List<String> str_providers =zkclient.getChildren(path.concat(CommonConfig.SLASH).concat(method));
        List<SharedProvider> SharedProviders =new ArrayList<>();

        for(String str_provider:str_providers) {

          SharedProvider sharedProvider =
              JsonConvert.toObject(zkclient.getContent(path.concat(CommonConfig.SLASH).concat(method).concat(CommonConfig.SLASH).concat(str_provider)),
                  SharedProvider.class);
          SharedProviders.add(sharedProvider);
        }
        if(SharedProviders.size()>0){
          LOGGER.info(method + "$");
          ProviderInvoker.init(method,SharedProviders);
        }
      }
      return ProviderInvoker;
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("初始化可用节点异常",e);
      return null;

    }
  }


  /**********************************/
  //调用
  public SharedProvider Function(String method){

    return versionedProviderInvokers.get(CommonConfig.DEFAULTVERSION).get(method,String.valueOf(System.currentTimeMillis()));
  }

  //TODO
  //get/post/delete/put等方法的实现
}
