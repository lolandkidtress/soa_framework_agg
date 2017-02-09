package com.James.Provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Annotation.tracking;
import com.James.Exception.zkConnectionException;
import com.James.JettyServer.JettyServer;
import com.James.MonitorHandle.trackingHandle;
import com.James.MonitorInstance;
import com.James.avroNettyServer.avroServer;
import com.James.avroProto.avrpRequestProto;
import com.James.avroServiceRegist.avroRequestHandleRegister;
import com.James.basic.Annotation.descriptionAnnotation;
import com.James.basic.Model.sharedNode;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.zkTools.zkClientTools;
import com.James.soa_agent.HotInjecter;

import UtilsTools.ClassScan;


/**
 * Created by James on 16/5/30.
 * 服务提供方
 */
public class providerInstance {
  private static final Log logger = LogFactory.getLog(providerInstance.class.getName());

  private static class InnerInstance {
    public static final providerInstance instance = new providerInstance();
  }

  public static providerInstance getInstance() {
    return InnerInstance.instance;
  }

  //需要注册到zk的服务
  private List<sharedNode> _sharedNodes = new ArrayList<>();

  //已扫描到的服务,用于重命检验
  private static Set readMethodName = new HashSet<String>();

  private String serverName;

  public String getServerName(){
    return this.serverName;
  }

  private providerInstance() {
  }

  private zkClientTools zkclient;
  private String zkConnect;

  private String defaultHttpPort = CommonConfig.defaultHttpPort;
  private String defaultAvroPort = CommonConfig.defaultAvroPort;
  private String defaultHttpContext ="/";


  public String getDefaultAvroPort(){
    return this.defaultAvroPort;
  }

  public String getDefaultHttpPort(){
    return this.defaultHttpPort;
  }

  public String getDefaultHttpContext(){
    return this.defaultHttpContext;
  }

  public providerInstance readConfig( Properties properties){

    this.zkConnect = properties.getProperty("zookeeper");

    if(this.zkConnect==null||this.zkConnect.length()<0){
      logger.error("没有配置zk连接");
      return null;
    }

    if(properties.getProperty("HttpPort")==null){
      logger.info("没有配置http端口,使用默认地址");
    }else{
      this.defaultHttpPort = properties.getProperty("HttpPort");
    }

    if(properties.getProperty("HttpContext")==null){
      logger.info("没有配置context,使用默认context");
    }else{
      this.defaultHttpContext = properties.getProperty("HttpContext");
    }

    logger.info("http端口为:" + this.defaultHttpPort);

    if(properties.getProperty("AvroPort")==null){
      logger.warn("没有配置rpc端口,使用默认地址");
    }else{
      this.defaultAvroPort = properties.getProperty("AvroPort");

    }

    logger.info("rpc端口为:" + this.defaultAvroPort);

    if(properties.getProperty("monitor").equals("true")){
      HotInjecter.getInstance().add_advice_method(tracking.class, new trackingHandle());
      HotInjecter.getInstance().advice();
      MonitorInstance.INSTANCE.runAsClient();
      logger.info("启动trackingMonitor");
    }
    return this;
  }

  public providerInstance startServer(String serverName) throws Exception{

    init(this.zkConnect);

    if(this.zkclient==null){
      logger.error("zookeeeper连接失败");
      throw new zkConnectionException();
    }

    this.serverName = serverName;

    //扫描所有含有descriptionAnnotation的类
    Set<Class<?>> providerClasses = (Set<Class<?>>) ClassScan.scanAnnotationClasses(descriptionAnnotation.class, providerScanImpl.class);
//    Set<Class<?>> providerClasses = providerScanner.scanClasses();

    providerClasses.forEach(providerClass -> {
      //读取注解信息
      logger.info("开始读取" + providerClass.getName() + "类下的注册信息");

      providerScanImpl.readClasses(providerClass,serverName).forEach(sharedProvider -> {
        if (sharedProvider.getIdentityID() != null) {
          //判断重名
          if (readMethodName.contains(sharedProvider.getIdentityID())) {
            logger.error(providerClass.getName() + "扫描到重复定义: " + sharedProvider.getIdentityID());
          } else {
            _sharedNodes.add(sharedProvider);
            readMethodName.add(sharedProvider.getIdentityID());
          }

        }
      });
    });

    //处理avro
    _sharedNodes.stream()
        .filter(sharedProvider -> sharedProvider.getProtocol().equals(CommonConfig.PROTOCOL.avro))
        .forEach(sharedProvider -> {
          try {

            //注册对应的avrpRequestProto类到avro处理器
            avroRequestHandleRegister.INSTANCE.addRequestHandle(sharedProvider.getMethod_name(),
                (avrpRequestProto) Class.forName(sharedProvider.getDeclaringClass_name()).newInstance());
//            avroRpcServer.getInstance().addRegisterServers("test",
//                (avrpRequestProto) Class.forName(sharedNode.getDeclaringClass_name()).newInstance());
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("ClassNotFoundException" + sharedProvider.getDeclaringClass_name());
          } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            logger.error("IllegalAccessException" + sharedProvider.getDeclaringClass_name());
          } catch (InstantiationException ise) {
            ise.printStackTrace();
            logger.error("InstantiationException" + sharedProvider.getDeclaringClass_name());
          }
        });

    //服务节点写入zk
    if(_sharedNodes.size()>0){

      //往zk中写入注册的服务
      providerRegister.INSTANCE.registerServers(_sharedNodes,zkclient);
      logger.info("注册自身服务结束");
    }else{
      logger.error("没有需要注册的服务");
    }

    try{
      //启动avro服务
      avroServer.startServer(Integer.valueOf(this.defaultAvroPort));
//      avroRpcServer.getInstance().startServer();
    }catch(IOException ioe){
      ioe.printStackTrace();
      logger.error("启动avro服务异常" );
    }

    try{
      //启动http服务
      JettyServer.startJetty(Integer.valueOf(this.defaultHttpPort),this.defaultHttpContext);
//      avroRpcServer.getInstance().startServer();
    }catch(IOException ioe){
      ioe.printStackTrace();
      logger.error("启动http服务异常" );
    }

    //TODO
    //退出时发通知给zk
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      logger.info("系统退出,关闭监听服务");
    }));

    return this;
  }


  public void init(String zkconnect,String providerMangerPath){

    if(!zkClientTools.isConnected(zkconnect)){
      logger.error("zookeeeper连接失败");

    }else{
      this.zkclient = new zkClientTools(zkconnect,providerMangerPath);
      logger.info("zookeeeper连接成功");
    }


  }

  public void init(String zkconnect) {

    if(!zkClientTools.isConnected(zkconnect)){
      logger.error("zookeeeper连接失败");
    }else{
      this.zkclient = new zkClientTools(zkconnect,"");
      logger.info("zookeeeper连接成功");
    }

  }




}
