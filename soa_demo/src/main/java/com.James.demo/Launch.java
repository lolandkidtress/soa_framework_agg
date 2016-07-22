package com.James.demo;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.James.Invoker.Invoker;
import com.James.Kafka_Tools.Kafka_Consumer;
import com.James.Kafka_Tools.Kafka_Producer;
import com.James.Provider.providerInstance;
import com.James.basic.UtilsTools.Parameter;
import com.James.demo.CodeInjection.hot_Injection;
import com.James.demo.Kafka.MsgCosum;
import com.James.kafka_Config.Configuration;

import UtilsTools.JsonConvert;


/**
 * Created by James on 16/7/21.
 */
public class Launch {

  private static Configuration configuration = null;
  static {
    Properties properties = new Properties();
    properties.put("zookeeper", "192.168.202.16:2181/kafka");
    properties.put("kafka","192.168.202.34:9092,192.168.202.35:9092,192.168.202.36:9092");

    try{
      configuration = Configuration.getInstance().initialization(properties);
    }catch(Exception e){
      e.printStackTrace();
    }


  }

  //代码注入sample
  public void hotInject(){
    hot_Injection injection = new hot_Injection();
    injection.inject();

    try{
      injection.buildString(2);
    }catch(Exception e){
      e.printStackTrace();
    }

  }

  //服务发现sample
  public void discovery(){
    //zookeeper地址
    String zkconnect = "192.168.202.16:2181/kafka";

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
    System.out.println("start 返回:" + demoinvoke.call("start", Parameter.create()));
    System.out.println("avrosend 返回:" + demoinvoke.call("avrosend", Parameter.create()));
  }

  //kafka收消息sample
  //消息处理在MsgCosum中处理
  public void receiveKafka(){

    System.out.println("start_consumer");
    Kafka_Consumer kafka_Consumer = new Kafka_Consumer();
    kafka_Consumer.consume(configuration, "soa_group_test", "largest", 2, "soa_test", MsgCosum.class);
  }

  //kafka发送消息sample
  public void sendKafka() throws Exception{

      System.out.println("start_producer");
      Kafka_Producer.getInstance().start(configuration);
      int i=0;

      while(i<=1000){
        i++;
        Kafka_Producer.getInstance().send("soa_test","key",String.valueOf(i));

        TimeUnit.SECONDS.sleep(1L);
      }
  }


  public static void main(String[] args) throws Exception {
    Launch launch = new Launch();

    launch.hotInject();
    launch.discovery();

    launch.receiveKafka();
    launch.sendKafka();

    System.exit(0);
  }


}
