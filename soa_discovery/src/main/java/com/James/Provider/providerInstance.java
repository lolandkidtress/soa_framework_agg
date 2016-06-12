package com.James.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Model.SharedProvider;
import com.James.zkTools.zkClientTools;


/**
 * Created by James on 16/5/30.
 * 服务提供方
 */
public class providerInstance {
  private static final Logger LOGGER = LoggerFactory.getLogger(providerInstance.class.getName());

  private static class InnerInstance {
    public static final providerInstance instance = new providerInstance();
  }

  public static providerInstance getInstance() {
    return InnerInstance.instance;
  }

  //需要注册的服务
  private List<SharedProvider> sharedProviders = new ArrayList<>();

  private String serverName;

  public String getServerName(){
    return this.serverName;
  }

  private providerInstance() {
  }

  private zkClientTools zkclient;
  private String zkConnect;

  private String defaultHttpPort = "9090";
  private String defaultAvroPort = "46111";
  private String defaultHttpContext ="";

  //
  public providerInstance readConfig( Properties properties){


    this.zkConnect = properties.getProperty("zkConnect");

    if(this.zkConnect==null||this.zkConnect.length()<0){
      LOGGER.error("没有配置zk连接");
      return null;
    }

    if(properties.getProperty("HttpPort")==null){
      LOGGER.error("没有配置http端口,使用默认地址");
    }else{
      this.defaultHttpPort = properties.getProperty("HttpPort");
    }

    LOGGER.info("http端口为:" + this.defaultHttpPort);

    if(properties.getProperty("AvroPort")==null){
      LOGGER.error("没有配置rpc端口,使用默认地址");
    }else{
      this.defaultAvroPort = properties.getProperty("AvroPort");
    }

    LOGGER.info("rpc端口为:" + this.defaultAvroPort);
    return this;
  }

  public providerInstance startServer(String serverName) {

    init(this.zkConnect);

    if(this.zkclient==null){
      LOGGER.error("zookeeeper连接失败");
      return null;
    }

    this.serverName = serverName;

    Set<Class<?>> providerClasses = providerScanner.scanClasses();
    providerClasses.forEach(providerClass -> {
      //读取注解信息
      LOGGER.info("开始扫描" + providerClass.getName() + "类");

      providerScanner.readClasses(providerClass).forEach(sharedProvider -> {
        if (sharedProvider.getIdentityID() != null) {
          sharedProvider.setHttp_port(this.defaultHttpPort);
          sharedProvider.setRpc_port(this.defaultAvroPort);
          sharedProvider.setHttp_context(this.defaultHttpContext);
          sharedProviders.add(sharedProvider);
        }
      });
    });

    if(sharedProviders.size()>0){

      //往zk中写入注册的服务
      providerRegister.INSTANCE.registerServers(sharedProviders,zkclient);
      LOGGER.info("注册自身服务结束");
    }else{
      LOGGER.error("没有需要注册的服务");
    }

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      LOGGER.info("系统退出,关闭监听服务");
    }));

    return this;
  }


  public void init(String zkconnect,String providerMangerPath){

    if(!zkClientTools.isConnected(zkconnect)){
      LOGGER.error("zookeeeper连接失败");

    }
    this.zkclient = new zkClientTools(zkconnect,providerMangerPath);
    LOGGER.info("zookeeeper连接成功");

  }

  public void init(String zkconnect){

    if(!zkClientTools.isConnected(zkconnect)){
      LOGGER.error("zookeeeper连接失败");

    }
    this.zkclient = new zkClientTools(zkconnect,"");
    LOGGER.info("zookeeeper连接成功");
  }




}
