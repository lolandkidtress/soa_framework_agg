package com.James.Provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Model.SharedProvider;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.zkInstance;
import com.James.zkTools.zkClientTools;


/**
 * Created by James on 16/5/30.
 * 服务注册器
 */
public enum providerRegister {
  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(providerRegister.class.getName());

  private zkClientTools zkclient;

  //TODO 本地服务的hostname,ip等信息

  //初始化zk连接
  public providerRegister init(){

    this.zkclient = zkInstance.INSTANCE.getZkclient();
    if(this.zkclient==null){
      LOGGER.error("zookeeeper连接失败");
      return null;
    }
    return this;
  }

  //注册服务
  public providerRegister registerServers(List<SharedProvider> sharedProviders){
    try{
      if(!this.zkclient.checkExists(zkInstance.INSTANCE.providerMangerPath)){
        this.zkclient.createPERSISTENTNode(zkInstance.INSTANCE.providerMangerPath);
      }
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("创建目录错误",e);
      return null;
    }


    //节点信息写入zk
    // 目录格式: providers/{服务名}/{version}/{方法名}/{identityID}
    sharedProviders.forEach(sharedProvider -> {

      //写2处
      //非默认版本号
      if (!sharedProvider.isDefaultVersion()) {
        StringBuilder sb = new StringBuilder();
        sb = createServerPath(sharedProvider);

        sb = createVersionPath(sb, sharedProvider,false);
        sb = createMethodPath(sb, sharedProvider);
        sb = createMethodNode(sb, sharedProvider);
        setServerData(sb, sharedProvider);
      }

      //默认版本
      StringBuilder sb = new StringBuilder();
      sb = createServerPath(sharedProvider);

      sb = createVersionPath(sb, sharedProvider,true);
      sb = createMethodPath(sb, sharedProvider);
      sb = createMethodNode(sb, sharedProvider);
      setServerData(sb, sharedProvider);


    });

    Map<String,String> map = new HashMap();
    map.put("updated", String.valueOf(System.currentTimeMillis()));
    try{
      this.zkclient.setContent(zkInstance.INSTANCE.providerMangerPath.concat(CommonConfig.SLASH.concat(providerInstance.getInstance().getServerName())) , JsonConvert.toJson(map));

    }catch (Exception e){
      e.printStackTrace();
      LOGGER.error("更新根节点数据错误",e);
    }


    return this;
  }

  //创建Server的path路径
  private StringBuilder createServerPath(SharedProvider sharedProvider){
    try{
      StringBuilder sb = new StringBuilder();
      sb.append(zkInstance.INSTANCE.providerMangerPath);
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getServer_name());

      if(!this.zkclient.checkExists(sb.toString() )){
        this.zkclient.createPERSISTENTNode(sb.toString());
      }
      return sb;
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("创建目录错误", e);
      return null;
    }
  }

  //创建Server / defaultVersion 的path路径
  private StringBuilder createVersionPath(StringBuilder sb,SharedProvider sharedProvider,boolean isDefault){
    try{

      sb.append(CommonConfig.SLASH);
      if(isDefault){
        sb.append(CommonConfig.DEFAULTVERSION);
      }else{
        sb.append(sharedProvider.getVersion());
      }


      if(!this.zkclient.checkExists(sb.toString()) ){
        this.zkclient.createPERSISTENTNode(sb.toString());
      }
      return sb;
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("创建目录错误", e);
      return null;
    }
  }

  //创建Server / version/ method 的path路径
  private StringBuilder createMethodPath(StringBuilder sb,SharedProvider sharedProvider){
    try{

      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getMethod_name());

      if(!this.zkclient.checkExists(sb.toString()) ){
        this.zkclient.createPERSISTENTNode(sb.toString());
      }
      return sb;

    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("创建目录错误", e);
      return null;
    }
  }

  //method 的服务节点
  private StringBuilder createMethodNode(StringBuilder sb,SharedProvider sharedProvider){
    try{

      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getIdentityID());

      if(!this.zkclient.checkExists(sb.toString()) ){
        this.zkclient.createEPHEMERALNode(sb.toString());
      }
      return sb;
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("创建目录错误", e);
      return null;
    }
  }

  //写入数据
  private void setServerData(StringBuilder sb,SharedProvider sharedProvider){
    try{

      this.zkclient.setContent(sb.toString(), JsonConvert.toJson(sharedProvider));

      LOGGER.info(sb.toString() +"写入服务节点成功");
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("写入服务节点失败", e);
    }
  }

}
