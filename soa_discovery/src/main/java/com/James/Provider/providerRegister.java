package com.James.Provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Model.SharedProvider;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.zkTools.zkClientTools;


/**
 * Created by James on 16/5/30.
 * 服务注册器
 */
public enum providerRegister {
  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(providerRegister.class.getName());

  private zkClientTools zkclient;

  private String providerMangerPath ="/providers";



  //TODO 本地服务的hostname,ip等信息

  //初始化zk连接
  public providerRegister init(String zkconnect){

    if(!zkClientTools.isConnected(zkconnect)){
      LOGGER.error("zookeeeper连接失败");
      return null;
    }
    this.zkclient = new zkClientTools(zkconnect);
    LOGGER.info("zookeeeper连接成功");
    return this;
  }

  //注册服务
  public providerRegister registerServers(List<SharedProvider> sharedProviders){
    try{
      if(!this.zkclient.checkExists(providerMangerPath)){
        this.zkclient.createPERSISTENTNode(providerMangerPath);
      }
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("创建目录错误",e);
      return null;
    }

    //节点信息写入zk
    sharedProviders.forEach(sharedProvider -> {
      createServerPath(sharedProvider);
      createServerNode(sharedProvider);
      createMethodNode(sharedProvider);
      setServerData(sharedProvider);
    });

    return this;
  }

  //创建Server的path路径
  private void createServerPath(SharedProvider sharedProvider){
    try{
      StringBuilder sb = new StringBuilder();
      sb.append(providerMangerPath);
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getServer_name());

      if(!this.zkclient.checkExists(sb.toString() )){
        this.zkclient.createPERSISTENTNode(sb.toString());
      }
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("创建目录错误", e);
    }
  }

  //创建Server + method 的path路径
  private void createServerNode(SharedProvider sharedProvider){
    try{

      StringBuilder sb = new StringBuilder();
      sb.append(providerMangerPath);
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getServer_name());
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getMethod_name());

      if(!this.zkclient.checkExists(sb.toString()) ){
        this.zkclient.createPERSISTENTNode(sb.toString());
      }
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("创建目录错误", e);
    }
  }

  //method 的服务节点
  private void createMethodNode(SharedProvider sharedProvider){
    try{

      StringBuilder sb = new StringBuilder();
      sb.append(providerMangerPath);
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getServer_name());
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getMethod_name());
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getIdentityID());

      if(!this.zkclient.checkExists(sb.toString()) ){
        this.zkclient.createEPHEMERALNode(sb.toString());
      }
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("创建目录错误", e);
    }
  }

  //写入数据
  private void setServerData(SharedProvider sharedProvider){
    try{

      StringBuilder sb = new StringBuilder();
      sb.append(providerMangerPath);
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getServer_name());
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getMethod_name());
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getIdentityID());

      this.zkclient.setContent(sb.toString(), JsonConvert.toJson(sharedProvider));

      LOGGER.debug("写入服务节点成功");
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("写入服务节点失败", e);
    }
  }

}
