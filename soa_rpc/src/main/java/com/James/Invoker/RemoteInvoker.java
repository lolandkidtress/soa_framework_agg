package com.James.Invoker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Listeners.nodeReloadListenerImpl;
import com.James.RemoteCall.remoteCallHelper;
import com.James.basic.Annotation.mockAnnotation;
import com.James.basic.Enum.Code;
import com.James.basic.Exception.Method_Not_Found_Exception;
import com.James.basic.Invoker.Invoker;
import com.James.basic.Model.provider;
import com.James.basic.Model.sharedNode;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Parameter;
import com.James.basic.UtilsTools.Return;
import com.James.basic.zkTools.zkClientTools;
import com.James.basic.zkTools.zkWatchInstance;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Created by James on 16/6/2.
 * 服务调用方
 */
public class RemoteInvoker implements Invoker,Serializable {


  private static final long serialVersionUID = 1L;

  private static final Log LOGGER = LogFactory.getLog(Invoker.class.getName());

  private zkClientTools zkclient;



  @Override
  public Class getInterface() {
    return this.getClass();
  }

  public RemoteInvoker(String server_name, String zkconnect) {

    if (!zkClientTools.isConnected(zkconnect)) {
      LOGGER.error("zookeeeper连接失败");
    }
    this.zkclient = new zkClientTools(zkconnect, "");
    LOGGER.info("zookeeeper连接成功");
    //this.mockIns = new mockInstance(zkconnect);

    initInvoker(server_name);

  }

  public RemoteInvoker(String server_name, zkClientTools zkclient) {

    this.zkclient = zkclient;
    initInvoker(server_name);
  }

  public RemoteInvoker(String server_name, String zkconnect, String namespace) {

    if (!zkClientTools.isConnected(zkconnect)) {
      LOGGER.error("zookeeeper连接失败");
    }
    this.zkclient = new zkClientTools(zkconnect, namespace);
    LOGGER.info("zookeeeper连接成功");

    initInvoker(server_name);

  }

  public static RemoteInvoker create(String server_name,String zkconnect){
    return new RemoteInvoker(server_name,zkconnect);
  }

  public static RemoteInvoker create(String server_name,zkClientTools zkclient){
    return new RemoteInvoker(server_name,zkclient);
  }

  public static RemoteInvoker create(String server_name,String zkconnect,String namespace){
    return new RemoteInvoker(server_name,zkconnect,namespace);
  }

  //  SLASH开始的服务名
  private void initInvoker(String server_name ){

    if(!server_name.startsWith(CommonConfig.SLASH)){
      server_name = CommonConfig.SLASH.concat(server_name);
    }
    try{
      if(!this.zkclient.checkExists(server_name)){
        LOGGER.error("没有名称为" + server_name + "的服务提供者");
      }else{
        LOGGER.info("开始扫描" + server_name + "服务提供的接口");

//        StringBuilder sb = new StringBuilder();
//        sb.append(CommonConfig.SLASH);
//        sb.append(server_name);

        //扫描zk路径,根据版本方法名生成hash环
        buildNodeGroup(server_name);
        //添加watch
        zkWatchInstance.getInstance().watch(server_name,this.zkclient.getCuratorFramework(),new nodeReloadListenerImpl());
      }

      InvokerHelper.getInstance().setWatchedInvokers(server_name, this);

    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("zookeeeper连接异常");
    }

  }

  //************************************//

  //保存带版本号的服务节点组
  //key:method,value:provider
  private ConcurrentHashMap<String,provider> methodProviderInvokers= new ConcurrentHashMap();

  //扫描不同的版本下面方法和注册的节点,生成hash环
  private ConcurrentHashMap<String,provider> buildNodeGroup(String path){
    try{
      List<String> identityIDs =  zkclient.getChildren(path);

      for(String id : identityIDs){
        String method = id.split(CommonConfig.HYPHEN)[1];
        LOGGER.info("取得method:" + method);
        //
        buildMethodGroup(method,path.concat(CommonConfig.SLASH).concat(id));

      }
      return methodProviderInvokers;
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("初始化可用节点异常",e);
      return null;
   }

  }

  /**
   * @param path
   *
   * @return
   */
  //方法下的节点做一致性hash
  private void buildMethodGroup(String method,String path){
    try{

      String data = zkclient.getContent(path);

      Map<String,Map> version_data_pair = new HashMap<>();

      version_data_pair = JsonConvert.toObject(data, Map.class);

      Iterator<String> ite_version = version_data_pair.keySet().iterator();

      provider Provider = methodProviderInvokers.getOrDefault(method,new provider());

      while(ite_version.hasNext()) {

        String ver = ite_version.next();
        Map<String,String> s_node = version_data_pair.get(ver);
        sharedNode sharedNode = JsonConvert.toObject(JsonConvert.toJson(s_node), sharedNode.class);

        LOGGER.info("组装"+ver+"的hash环");
        Provider.init(ver, sharedNode);

        methodProviderInvokers.put(method, Provider);
        InvokerHelper.getInstance().addWatchedProvider(sharedNode);

      }

      LOGGER.info("初始化可用节点完成");

    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("初始化可用节点异常",e);

    }
  }

  /**********************************/


  //调用
  public Return call(String method,String version,Parameter parameter){

    if(version==null||version.length()==0){
      version = CommonConfig.DEFAULTVERSION;
    }

    sharedNode SharedNode;
    try{
      if(methodProviderInvokers.get(method)!=null){
        SharedNode = methodProviderInvokers.get(method).get(version,
            parameter.get("trackingID"));
      }else{
        LOGGER.error("没有可用服务节点");
        return Return.FAIL(Code.node_unavailable.code,Code.node_unavailable.name());
      }

    }catch(Method_Not_Found_Exception e){
      e.printStackTrace();
      LOGGER.error("没有可用服务节点",e);
      return Return.FAIL(Code.node_unavailable.code,Code.node_unavailable.name());
    }catch(Exception e1){
      e1.printStackTrace();
      LOGGER.error("系统异常",e1);
      return Return.FAIL(Code.error.code,Code.error.name());
    }

    //事前拦截
    //配置过调用前降级,且已降级
    if(SharedNode.getMockPolicy()!=null
      && SharedNode.getMockPolicy().getPolicy()==mockAnnotation.Policy.Call_RETURN
      //&& this.mockIns.validateMockState(SharedNode.getMockPolicy().getName())
        ){

      return SharedNode.getMockPolicy().getMockReturn();
    }else {

      Return ret = callImpl(SharedNode, method, parameter);

      //成功直接返回
      if (ret.is_success()){
        return ret;
      }

      //配置过降级策略
      if (SharedNode.getMockPolicy() != null) {
        //记录调用失败
          //this.mockIns.failIncr(SharedNode.getMockPolicy());

          //事后拦截
          if (SharedNode.getMockPolicy().getPolicy() == mockAnnotation.Policy.Fail_RETURN) {
            return SharedNode.getMockPolicy().getMockReturn();
          } else {
            return ret;
          }
        }else{
          return ret;
        }
      }


  }

  private Return callImpl(sharedNode SharedNode,String method,Parameter parameter) {
    Return ret ;

    switch (SharedNode.getProtocol()) {
      case http :
        ret = remoteCallHelper.http_call(SharedNode, parameter);
        return ret;
      case avro:
        ret = remoteCallHelper.avro_call(SharedNode, parameter);
        return ret;
//        case protoc:
//          //TODO
//          break;
//        case thrift :
//          //TODO
//          break;
      default:
        LOGGER.error(method + "不支持的协议");
        ret = Return.FAIL(Code.protocol_notsupport.code,Code.protocol_notsupport.name());
    }

    return ret;
  }

  //随机取得可用节点
  public sharedNode getAvailableProvider(String method){
    try{

      return methodProviderInvokers.get(method).get(CommonConfig.DEFAULTVERSION,String.valueOf(System.currentTimeMillis()));
    }catch(Method_Not_Found_Exception e){
      e.printStackTrace();
      LOGGER.error("没有可用服务节点");
      return null;
    }

  }

  //取得固定的某个节点
  public sharedNode getAvailableProvider(String method,String seed){
    try{
      return methodProviderInvokers.get(method).get(CommonConfig.DEFAULTVERSION,seed);
    }catch(Method_Not_Found_Exception e){
      e.printStackTrace();
      LOGGER.error("没有可用服务节点");
      return null;
    }

  }


  //setter/getter

  public zkClientTools getZkclient() {
    return zkclient;
  }

  public void setZkclient(zkClientTools zkclient) {
    this.zkclient = zkclient;
  }

  public ConcurrentHashMap<String, provider> getMethodProviderInvokers() {
    return methodProviderInvokers;
  }

  public void setMethodProviderInvokers(ConcurrentHashMap<String, provider> methodProviderInvokers) {
    this.methodProviderInvokers = methodProviderInvokers;
  }

}
