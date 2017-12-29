package com.James;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Kafka_Tools.Kafka_Consumer;
import com.James.Kafka_Tools.Kafka_Producer;
import com.James.basic.Model.TrackingChain;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.kafkaConsumeHandle.TrackingSelfHandle;
import com.James.kafka_Config.Configuration;


/**
 * Created by James on 2017/2/4.
 */
public enum  MonitorInstance {

  INSTANCE;
  private final static Logger logger = LoggerFactory.getLogger(MonitorInstance.class.getName());
  //服务标记
  //以self启动时,只记录对应clientID的记录消息
  //以server启动时,记录所有监控记录
  private final static String clientID= CommonConfig.clientID;
  private Kafka_Producer Producer = null;
  private Kafka_Consumer Consumer = null;
  private String trackingTopic = "tracking";
  private static Configuration configuration = Configuration.getInstance();

  MonitorInstance(){
  }

  //接收相同clientID的消息做统计
  public void startTrackingSelf(){
    Consumer = new Kafka_Consumer();
    Consumer.init("TrackingSelfHandle",clientID,configuration.kafka,trackingTopic);
    Consumer.consume(trackingTopic, TrackingSelfHandle.class);
  }

  //客户端模式,只发送消息
  public void runAsClient(){
    Producer = Kafka_Producer.getInstance().init(configuration,trackingTopic);
    logger.info("trackingMonitor Producer启动");
//    Consumer = new Kafka_Consumer();
//    Consumer.consume(configuration, "group", "largest", 2, trackingTopic, TrackingAsServerHandle.class);
  }

  //server端模式,接收所有消息做统计
  public void runAsServer(){
    Consumer = new Kafka_Consumer();
    Consumer.init("TrackingAsServerHandle",clientID,configuration.kafka,trackingTopic);
    Consumer.consume(trackingTopic, TrackingSelfHandle.class);
  }

  public void send2kafka(TrackingChain trackingChain){
    if(Producer!=null){
      Producer.send(trackingTopic,clientID,trackingChain.toJson());
    }

  }

  public String getClientID() {
    return clientID;
  }
}
