package com.James.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Model.SharedProvider;


/**
 * Created by James on 16/5/30.
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

  public providerInstance startServer(String serverName,String zkConnect) {


    this.serverName = serverName;

    Set<Class<?>> providerClasses = providerScanner.scanClasses();
    providerClasses.forEach(providerClass -> {
      //读取注解信息
      LOGGER.info("开始扫描" + providerClass.getName() + "类");
      providerScanner.readClasses(providerClass).forEach(sharedProvider -> {
        if (sharedProvider.getIdentityID() != null) {
          sharedProviders.add(sharedProvider);
        }
      });
    });

    if(sharedProviders.size()>0){
      //初始化zk
      providerRegister.INSTANCE.init(zkConnect);
      //往zk中写入注册的服务
      providerRegister.INSTANCE.registerServers(sharedProviders);
      LOGGER.info("注册自身服务结束");
    }else{
      LOGGER.error("没有需要注册的服务");
    }

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      LOGGER.info("系统退出,关闭监听服务");
    }));

    return this;
  }




}
