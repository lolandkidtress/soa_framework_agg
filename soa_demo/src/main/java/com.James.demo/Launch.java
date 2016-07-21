package com.James.demo;

import java.util.Properties;

import com.James.Invoker.Invoker;
import com.James.Provider.providerInstance;
import com.James.basic.UtilsTools.Parameter;
import com.James.demo.CodeInjection.hot_Injection;

import UtilsTools.JsonConvert;


/**
 * Created by James on 16/7/21.
 */
public class Launch {

  public void hotInject(){
    hot_Injection injection = new hot_Injection();
    injection.inject();

    try{
      injection.buildString(2);
    }catch(Exception e){
      e.printStackTrace();
    }

  }

  public void discovery(){
    //zookeeper地址
    String zkconnect = "172.16.8.97:2181";

    Properties properties = new Properties();
    properties.setProperty("zkConnect",zkconnect);

    //服务提供方的服务名称
    providerInstance.getInstance().readConfig(properties).startServer("com.James.demo");
    //调用方
    Invoker demoinvoke = Invoker.create("com.James.demo",zkconnect);

    //取得远端服务的可用节点
    System.out.println(JsonConvert.toJson(demoinvoke.getAvailableProvider("start")));
    System.out.println(JsonConvert.toJson(demoinvoke.getAvailableProvider("avrosend")));

    //调用2个接口
//    System.out.println("start 返回:" + demoinvoke.call("start", Parameter.create()));
    System.out.println("avrosend 返回:" + demoinvoke.call("avrosend", Parameter.create()));
  }

  public static void main(String[] args) throws Exception {
    Launch launch = new Launch();

    launch.hotInject();
    launch.discovery();

    System.exit(0);
  }


}
