package com.James.Kafka_Tools;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.James.kafka_Config.Configuration;

/**
 * Created by James on 16/5/20.
 * kafka 生产者
 */
public class Kafka_Producer {
    private static final Log LOGGER = LogFactory.getLog(Kafka_Producer.class.getName());

    private static Properties props = new Properties();

    private static class InnerInstance {
        public static final Kafka_Producer instance = new Kafka_Producer();
    }

    public static Kafka_Producer getInstance() {
        return InnerInstance.instance;
    }

    private Kafka_Producer() {

    }

    private Producer<String, String> producer;

    // 启动kafka生产者
    public Kafka_Producer init(Configuration configuration) {

        if (configuration.kafka == null || configuration.kafka.trim().isEmpty()) {
            LOGGER.error("没有配置 kafka");
            return this;
        }

        //http://kafka.apache.org/documentation.html#producerconfigs
        this.props = new Properties();
        // 触发acknowledgement机制,数据完整性相关
        // 值为0,1,all,可以参考
        this.props.put("acks", "all");
        this.props.put("retries", 3);
        this.props.put("batch.size", 16384);
        this.props.put("linger.ms", 1);
        this.props.put("compression.type","gzip");
        this.props.put("buffer.memory", 33554432);
        this.props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        this.props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafka);
        this.props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.clientId);
        //props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        this.props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);

        return this;
    }

    // 关闭一个kafka生产者
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }

    public void send(String topic, String key, String message) {
        if (this.producer != null) {
            try {
                RecordMetadata ret = this.producer.send(new ProducerRecord<String, String>(topic, key, message)).get();

            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("kafka 写入失败", e);
            }
        } else {
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
}
