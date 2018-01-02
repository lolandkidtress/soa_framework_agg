package com.James.Kafka_Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.James.basic.UtilsTools.JsonConvert;
import com.James.kafka_Config.Configuration;


/**
 * Created by James on 16/5/20.
 * kafka 生产者
 */
public class Kafka_Producer {
    private static final Logger LOGGER = LogManager.getLogger(Kafka_Producer.class.getName());

    //private static Properties props = new Properties();

    //topic和生产对应关系
    private ConcurrentHashMap<String,Producer<String, String>> topicMap = new ConcurrentHashMap<>();

    private static class InnerInstance {
        public static final Kafka_Producer instance = new Kafka_Producer();
    }

    public static Kafka_Producer getInstance() {
        return InnerInstance.instance;
    }

    private Kafka_Producer() {

    }

    // 启动kafka生产者

    public Kafka_Producer init(String kafka,String clientId,String topic) {

        if(topicMap.containsKey(topic)){
            return this;
        }

        //http://kafka.apache.org/documentation.html#producerconfigs
        Properties props = new Properties();
        // 触发acknowledgement机制,数据完整性相关
        // 值为0,1,all,可以参考
        props.put("acks", "all");
        props.put("retries", 3);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("compression.type","gzip");
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        //props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        topicMap.put(topic,producer);
        LOGGER.info("生产端初始化成功");
        return this;
    }


    public Kafka_Producer init(Configuration configuration,String topic) {

        if (configuration.kafka == null || configuration.kafka.trim().isEmpty()) {
            LOGGER.error("没有配置 kafka");
            return this;
        }

        if(topicMap.containsKey(topic)){
            return this;
        }

        //http://kafka.apache.org/documentation.html#producerconfigs
        Properties props = new Properties();
        // 触发acknowledgement机制,数据完整性相关
        // 值为0,1,all,可以参考
        props.put("acks", "all");
        props.put("retries", 3);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("compression.type","gzip");
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafka);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.clientId);
        //props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        topicMap.put(topic,producer);
        LOGGER.info("生产端初始化成功");
        return this;
    }

    // 关闭一个kafka生产者
    public void close(String topic) {
        if(topicMap.containsKey(topic)){
            Producer<String, String> producer = topicMap.get(topic);
            producer.close();
            topicMap.remove(topic);
        }
    }

    public void send(String topic, String key, String message) {
        if(topicMap.containsKey(topic)){
            Producer<String, String> producer = topicMap.get(topic);
            if (producer != null) {
                try {
                    RecordMetadata ret = producer.send(new ProducerRecord<String, String>(topic, key, message)).get();

                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.info("kafka 写入失败", e);
                }
            } else {
                LOGGER.info("kafka未初始化");
            }
        }else{

            LOGGER.info("kafka未初始化");
        }

    }

//    public void sendAsync(String topic,String key, String message) {
//        while (true) {
//            String messageStr = "Message_" + message;
//            long startTime = System.currentTimeMillis();
//            producer.send(new ProducerRecord<>(topic,
//                key, message), new DemoCallBack(startTime, messageNo, messageStr));
//        }
//    }

    public static void main(String[] args) {
        org.apache.log4j.BasicConfigurator.configure();
        Properties properties = new Properties();
        //properties.put("zookeeper", "10.81.23.103:2181,10.81.23.104:2181,10.81.23.105:2181");
        properties.put("kafka","localhost:9092");
        properties.put("clientId","testclient");
        properties.put("group","testclient");

        Configuration configuration = null;
        try{
            configuration = Configuration.getInstance().initialization(properties);
            Properties props = new Properties();
            // 触发acknowledgement机制,数据完整性相关
            // 值为0,1,all,可以参考
            props.put("acks", "all");
            props.put("retries", 3);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("compression.type","gzip");
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafka);
            props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.clientId);
            //props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            Producer<String, String> producer = new KafkaProducer<>(props);
            Map d = new HashMap<>();
            d.put("code","");
            d.put("ip",null);
            try {
                int i=0;
                while(i<100){
                    RecordMetadata ret = producer.send(
                        new ProducerRecord<String, String>("topic_metric_tracking", "key", JsonConvert.toJson(d))).get();
                    System.out.println("写入成功");
                    TimeUnit.SECONDS.sleep(1);
                    i++;
                    //break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("kafka 写入失败", e);
            }
            LOGGER.info("生产端初始化成功");
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("初始化Config异常");
        }


    }
}
