package com.James.kafka_Admin.AdminClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by James on 2018/1/1.
 */
public class RemoteAdminClient {

  private static final Logger LOGGER = LogManager.getLogger(RemoteAdminClient.class.getName());
  //5s
  private int default_timeout = 5;

  private AdminClient client ;
  private String bootstrap;
  private Properties properties;

  public RemoteAdminClient init(Properties props){
    if(!props.containsKey(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG)){
      LOGGER.error("没有配置kafka地址");
    }else{
      this.properties = props;
      try{
        client = AdminClient.create(props);
      }catch(Exception e){
        e.printStackTrace();
        LOGGER.error("初始化异常",e);
      }
    }


    return this;
  }

  public RemoteAdminClient init(String kafka){
    this.bootstrap = kafka;
    this.properties = new Properties();
    this.properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,kafka);
    init(this.properties);
    return this;
  }


  public static void main(String[] args) {

    RemoteAdminClient rac = new RemoteAdminClient();
    rac.init("localhost:9092");
    System.out.println("ClusterId:" + rac.getClusterId());
    rac.getClusters().forEach(node->{
      System.out.println("cluster @:"+node.host());
    });

    System.out.println("ControllerId:" +  rac.getControllerId());

    rac.getTopicNames().forEach(topic->{
      System.out.println("has topic :"+topic);
    });
    rac.getTopicNames().forEach(topicName -> {
      Map<ConfigResource, Config> configs = rac.getTopicConfig(topicName);

      for (Map.Entry<ConfigResource, Config> entry : configs.entrySet()) {
        ConfigResource key = entry.getKey();
        Config value = entry.getValue();
        System.out.println(String.format("Resource type: %s, resource name: %s", key.type(), key.name()));
        Collection<ConfigEntry> configEntries = value.entries();
        for (ConfigEntry each : configEntries) {
          System.out.println(each.name() + " = " + each.value());
        }
      }
    });

  }



  public String getClusterId() {
    return getClusterId(client,5);
  }

  public String getClusterId(AdminClient client,int timeout) {
    try {
      DescribeClusterResult ret = client.describeCluster();
      return ret.clusterId().get(timeout,TimeUnit.SECONDS);
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("获取clusterId异常",e);
      return "";
    }
  }

  public Node getControllerId() {
    return getControllerId(client,default_timeout);
  }

  public Node getControllerId(AdminClient client,int timeout) {
    try {
      DescribeClusterResult ret = client.describeCluster();
      return ret.controller().get(timeout,TimeUnit.SECONDS);
    }catch(Exception e) {
      e.printStackTrace();
      LOGGER.error("获取controller异常", e);
      return null;
    }
  }

  /**
   * describe the cluster
   */
  public Collection<Node> getClusters(){
    return getClusters( client,default_timeout);
  }

  public Collection<Node> getClusters(AdminClient client,int timeout){
    try{

      DescribeClusterResult ret = client.describeCluster();
      Collection<Node> ls = ret.nodes().get(timeout, TimeUnit.SECONDS);
      if(ls.size()>0){
        return ls;
      }else{
        return new ArrayList<>();
      }

    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("获取Cluster列表异常",e);
      return new ArrayList<>();
    }
  }

  public boolean createTopic(String topicName){
    CreateTopicsOptions options = new CreateTopicsOptions();
    return createTopic(client,topicName, 3, 3, options, default_timeout);

  }

  public boolean createTopic(String topicName,CreateTopicsOptions options){
    return createTopic(client,topicName, 3, 3, options, default_timeout);
  }

  public boolean createTopic(AdminClient client,String topicName,int numPartitions, int replicationFactor,CreateTopicsOptions options,int timeout){
    try {
      NewTopic newTopic = new NewTopic(topicName, numPartitions, (short) replicationFactor);
      CreateTopicsResult ret = client.createTopics(Arrays.asList(newTopic), options);
      ret.all().get(timeout,TimeUnit.SECONDS);
      return true;
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("获取TopicName列表异常", e);
      return false;
    }
  }
  public boolean deleteTopics(String topicName){
    return deleteTopics(client,topicName,default_timeout);
  }


  public boolean deleteTopics(AdminClient client,String topicName,int timeout) {
    try {

      KafkaFuture<Void> futures = client.deleteTopics(Arrays.asList(topicName)).all();
      futures.get(timeout,TimeUnit.SECONDS);
      return true;
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("删除topic异常", e);
      return false;
    }
  }


  public Set<String> getTopicNames() {
    return getTopicNames(client, default_timeout);
  }

  public Set<String> getTopicNames(AdminClient client,int timeout) {
    try {

//      ListTopicsOptions options = new ListTopicsOptions();
//      options.listInternal(true); // includes internal topics such as __consumer_offsets
//      ListTopicsResult topics = client.listTopics(options);

      ListTopicsResult topics = client.listTopics();
      return topics.names().get(timeout,TimeUnit.SECONDS);


    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("获取TopicName列表异常", e);
      return new HashSet<>();
    }
  }

  public Map<ConfigResource, Config> getTopicConfig(String topic) {
    try {
      DescribeConfigsResult ret = client.describeConfigs(
          Collections.singleton(new ConfigResource(ConfigResource.Type.TOPIC, topic)));
      Map<ConfigResource, Config> configs = ret.all().get(default_timeout,TimeUnit.SECONDS);

      return configs;
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("获取TopicConfig异常", e);
      return new HashMap<>();
    }

  }


  /**
   * alter config for topics
   */
  public boolean alterConfigs(String topic,String key,String value) {
    return  alterConfigs( client, topic, key, value,default_timeout);
  }

  public boolean alterConfigs(AdminClient client,String topic,String key,String value,int timeout) {
    try {
      //Config topicConfig = new Config(Arrays.asList(new ConfigEntry("cleanup.policy", "compact")));
      Config topicConfig = new Config(Arrays.asList(new ConfigEntry(key, value)));
      client.alterConfigs(Collections.singletonMap(new ConfigResource(ConfigResource.Type.TOPIC, topic), topicConfig))
          .all().get(timeout, TimeUnit.SECONDS);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static void describeBrokerConfig(AdminClient client) throws ExecutionException, InterruptedException {
      //TODO
  }

}
