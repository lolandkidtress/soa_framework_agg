package com.James.Invoker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Filter.Filter;
import com.James.InvokerMonitor.InvokerStatus;
import com.James.Listeners.nodeReloadListenerImpl;
import com.James.RemoteCall.remoteCallHelper;
import com.James.avroNettyClientConnect.avroNettyClientConnectionManager;
import com.James.basic.Enum.Code;
import com.James.basic.Exception.Method_Not_Found_Exception;
import com.James.basic.Invoker.Invoker;
import com.James.basic.Model.provider;
import com.James.basic.Model.sharedNode;
import com.James.basic.Model.trackingChain;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Parameter;
import com.James.basic.UtilsTools.Return;
import com.James.basic.UtilsTools.ThreadLocalCache;
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
    }else{
      this.zkclient = new zkClientTools(zkconnect, "");
      LOGGER.info("zookeeeper连接成功");
      //this.mockIns = new mockInstance(zkconnect);

      initInvoker(server_name);
    }


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

      InvokerStatus.setWatchedInvokers(server_name, this);

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
        sharedNode SharedNode = JsonConvert.toObject(JsonConvert.toJson(s_node), sharedNode.class);
        //有avro就初始化连接池
        if(SharedNode.getProtocol().equals(CommonConfig.PROTOCOL.avro)){
          avroNettyClientConnectionManager.getInstance().initConnectionPool(SharedNode);
        }
        LOGGER.info("组装"+ver+"的hash环");
        Provider.init(ver, SharedNode);

        methodProviderInvokers.put(method, Provider);
        InvokerStatus.addWatchedProvider(SharedNode);

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

    //节点信息转换
    sharedNode SharedNode;
    try{
      if(methodProviderInvokers.get(method)!=null){

        //同一个的trackingID能匹配同一个服务
        String seed = parameter.get(CommonConfig.s_trackingID);
        SharedNode = methodProviderInvokers.get(method).get(version, seed);
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

    String ratelimitName = (String) SharedNode.getFilterMap().getOrDefault("ratelimit","");
    String degradeName = (String) SharedNode.getFilterMap().getOrDefault("degrade","");
    //检查限流
    if(!ratelimitName.equals("")){
      //限流就返回配置的return对象
      if(!Filter.getInstance().isPassedRateLimit(ratelimitName)){
          return Filter.getInstance().getLimitConfig(ratelimitName).getDefaultReturn();
      }
    }

    //检查降级
    if(!degradeName.equals("")){
      //降级就返回配置的return对象
      if(!Filter.getInstance().isPassedDegrade(degradeName)){
        return Filter.getInstance().getDegradeCountDown(degradeName).getDefaultReturn();
      }
    }

    //theadlocalcache记录调用链
    trackingChain tc = ThreadLocalCache.getCallchain().get();
    if(tc==null){
      tc=new trackingChain(parameter.get(CommonConfig.s_trackingID));
    }
    tc.setInvokerID(CommonConfig.clientID);
    tc.setFromMethod(method);
    tc.setSequence(tc.getSequence() + 1);
    tc.setStart_time(System.currentTimeMillis());
    tc.setStatus(true);

    parameter.put(CommonConfig.s_sequence,String.valueOf(tc.getSequence()));
    parameter.put("targetRequest",method);
    Return InvokeRet = callImpl(SharedNode, method, parameter);

    //调用返回值不是正确的
    if(!InvokeRet.is_success() || (InvokeRet.get_code().equals(Code.error.code))){

      tc.setStatus(false);
      if(!degradeName.equals("")){
        Filter.getInstance().IncrDegradeCount(degradeName);
      }
    }

    tc.setEnd_time(System.currentTimeMillis());
    ThreadLocalCache.setCallchain(tc);

    return InvokeRet;

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
        ret = Return.FAIL(Code.protocol_not_support.code,Code.protocol_not_support.name());
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
