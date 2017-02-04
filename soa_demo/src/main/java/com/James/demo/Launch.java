package com.James.demo;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Invoker.RemoteInvoker;
import com.James.Kafka_Tools.Kafka_Consumer;
import com.James.Kafka_Tools.Kafka_Producer;
import com.James.Provider.providerInstance;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Parameter;
import com.James.demo.CodeInjection.hot_Injection;
import com.James.demo.Kafka.MsgCosum;
import com.James.demo.jettySpring.JettyServer;
import com.James.embeddedHttpServer.AppNanolets;
import com.James.kafka_Config.Configuration;



/**
 * Created by James on 16/7/21.
 */
public class Launch  {

  private static final Log logger = LogFactory.getLog(Launch.class.getName());
  private final static int httpPort = 8084;
  private final static int nanoPort = 9094;
  private final static int avroPort = 48084;

  private static Configuration configuration = null;
//  private static String zkconnect = "192.168.202.16:2181,192.168.202.17:2181,192.168.202.18:2181/kafka";
private static String zkconnect = "127.0.0.1:2181";
  private static String kafka = "127.0.0.1:9092";
  private static Properties properties = new Properties();

  static {

    properties.put("zookeeper", zkconnect);
    properties.put("kafka",kafka);

    try{
      configuration = Configuration.getInstance().initialization(properties);
    }catch(Exception e){
      e.printStackTrace();
    }


  }

  //代码注入sample
  public void hotInject(){

    logger.info("开始注入");
    hot_Injection injection = new hot_Injection();
    injection.inject();

    try{
      injection.buildString(2);

      logger.info("注入结束,应当能看到执行方法开始时间和总耗时");
    }catch(Exception e){
      e.printStackTrace();
    }

  }

  //服务发现sample
  public void registerServer(){

    logger.info("注册自身开始启动");

    //配置信息
    properties.setProperty("HttpPort", String.valueOf(httpPort));
    properties.setProperty("AvroPort", String.valueOf(avroPort));

    //服务提供方的服务名称
    try{
      providerInstance.getInstance().readConfig(properties).startServer("com.James.demo");
    }catch(Exception e){
      e.printStackTrace();
    }


  }

  //服务发现sample
  public void discoryServer() throws Exception{

    logger.info("服务发现开始启动");

    //调用方
    RemoteInvoker demoinvoke = RemoteInvoker.create("com.James.demo", zkconnect);

    //取得远端服务的可用节点
    logger.info("start的可用节点为:" + JsonConvert.toJson(demoinvoke.getAvailableProvider("start")));
    logger.info("avrosend的可用节点为"+ JsonConvert.toJson(demoinvoke.getAvailableProvider("avrosend")));

    //调用2个接口
    logger.info("start 返回:" + demoinvoke.call("start", "", Parameter.create().add("param1","参数1").add("param2","参数2")));
//    logger.info("start 返回:" + demoinvoke.call("start", "wrongVer",Parameter.create()));
    logger.info("avrosend 返回:" + demoinvoke.call("avrosend", "", Parameter.create()));

    logger.info("第一次调用限流接口 返回:" + demoinvoke.call("ratelimitCall","",Parameter.create()));
    logger.info("第二次调用限流接口 返回:" + demoinvoke.call("ratelimitCall","",Parameter.create()));
    //第三次code不同
    logger.info("第三次调用限流接口 返回:" + demoinvoke.call("ratelimitCall","",Parameter.create()));
    //等待一会自动恢复
    TimeUnit.SECONDS.sleep(6);
    logger.info("第四次调用限流接口 返回:" + demoinvoke.call("ratelimitCall", "", Parameter.create()));

    logger.info("第一次调用降级接口 返回:" + demoinvoke.call("degradeCall","",Parameter.create()));
//    TimeUnit.SECONDS.sleep(1);
    //第二次code不同
    logger.info("第二次调用降级接口 返回:" + demoinvoke.call("degradeCall","",Parameter.create()));
    TimeUnit.SECONDS.sleep(6);
    logger.info("第一次调用降级接口 返回:" + demoinvoke.call("degradeCall","",Parameter.create()));
//    TimeUnit.SECONDS.sleep(1);
    //第二次code不同
    logger.info("第二次调用降级接口 返回:" + demoinvoke.call("degradeCall","",Parameter.create()));

  }

  //kafka收消息sample
  //消息处理在MsgCosum中处理
  public void receiveKafka(){

    logger.info("kafka消费者开始工作");
    Kafka_Consumer kafka_Consumer = new Kafka_Consumer();
    kafka_Consumer.consume(configuration, "soa_group_test", "largest", 2, "soa_test", MsgCosum.class);
  }

  //kafka发送消息sample
  public void sendKafka() throws Exception{

      logger.info("kafka生产者开始工作");
      Kafka_Producer.getInstance().start(configuration);
      int i=0;

      while(i<=10){
        i++;
        Kafka_Producer.getInstance().send("soa_test","key",String.valueOf(i));

        TimeUnit.SECONDS.sleep(1L);
      }
  }


  public static void main(String[] args) throws Exception {
    Launch launch = new Launch();
    //代码注入
    launch.hotInject();

//    //指定http port
//    SpringApplication.run(Launch.class, args);
    JettyServer.startJetty(httpPort);
    TimeUnit.SECONDS.sleep(5);
    new AppNanolets(nanoPort);
    //在zk中注册http服务和avro服务
    launch.registerServer();
    //依赖上面注册的服务
    launch.discoryServer();
//    //kafka测试
    launch.receiveKafka();
    launch.sendKafka();



      Thread.currentThread().join();
    //System.exit(0);
  }



}
