package com.James;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.zkTools.zkClientTools;


/**
 * Created by James on 16/6/2.
 */
public enum zkInstance {
  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(zkInstance.class.getName());

  private zkClientTools zkclient;

  public String providerMangerPath ="/providers";

  public zkInstance init(String zkconnect){

    if(!zkClientTools.isConnected(zkconnect)){
      LOGGER.error("zookeeeper连接失败");
      return null;
    }
    this.zkclient = new zkClientTools(zkconnect);
    LOGGER.info("zookeeeper连接成功");
    return this;
  }

  public zkClientTools getZkclient(){
    return this.zkclient;
  }
}
