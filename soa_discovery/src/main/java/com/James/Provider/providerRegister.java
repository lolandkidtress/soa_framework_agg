package com.James.Provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Model.sharedNode;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.zkTools.zkClientTools;


/**
 * Created by James on 16/5/30.
 * 服务注册器
 */
public enum providerRegister {
  INSTANCE;

  private static final Log LOGGER = LogFactory.getLog(providerRegister.class.getName());

  private zkClientTools zkclient;
  //注册服务
  public providerRegister registerServers(List<sharedNode> sharedNodes,zkClientTools zkclient){
    this.zkclient = zkclient;

    //节点信息写入zk
    // 目录格式: /{服务名}/{identityID}
    sharedNodes.forEach(SharedNode -> {

      StringBuilder sb = new StringBuilder();
      sb = createServerPath(SharedNode);
      sb = creategetIdentityIDPath(sb,SharedNode);

      setServerData(sb, SharedNode);


    });

//    Map<String,String> map = new HashMap();
//    map.put("updated", String.valueOf(System.currentTimeMillis()));
//    try{
//      //更新根节点的时间,用于触发事件
//      this.zkclient.setContent(CommonConfig.SLASH.concat(providerInstance.getInstance().getServerName()) , JsonConvert.toJson(map));
//
//    }catch (Exception e){
//      e.printStackTrace();
//      LOGGER.error("更新根节点数据错误",e);
//    }


    return this;
  }

  //创建Server的path路径
  private StringBuilder createServerPath(sharedNode sharedNode){
    try{
      StringBuilder sb = new StringBuilder();
      //namespace方式制定根目录
//      sb.append(zkInstance.INSTANCE.providerMangerPath);
      sb.append(CommonConfig.SLASH);
      sb.append(sharedNode.getServer_name());

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
  private StringBuilder creategetIdentityIDPath(StringBuilder sb,sharedNode SharedNode){
    try{

      sb.append(CommonConfig.SLASH);
      sb.append(SharedNode.getIdentityID());


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
  //default版本和指定的版本
  //各写一份
  private void setServerData(StringBuilder pathsb,sharedNode SharedNode){
    try{

      Map<String,sharedNode> data = new HashMap<>();
      if (!SharedNode.isDefaultVersion()) {
        data.put(CommonConfig.DEFAULTVERSION, SharedNode);

      }

      data.put(SharedNode.getVersion(), SharedNode);
      this.zkclient.setContent(pathsb.toString(), JsonConvert.toJson(data));

      LOGGER.info(pathsb.toString() + "写入服务节点成功");
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("写入服务节点失败", e);
    }
  }

}
