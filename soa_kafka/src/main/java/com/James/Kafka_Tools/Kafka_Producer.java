package com.James.Kafka_Tools;

import com.James.Configuration.Configuration;
import com.James.Basic.UtilsTools.Logger_Once;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by James on 16/5/20.
 * kafka 生产者
 */
public class Kafka_Producer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Kafka_Producer.class.getName());

    private static Properties props = new Properties();

    private static class InnerInstance {
        public static final Kafka_Producer instance = new Kafka_Producer();
    }

    public static Kafka_Producer getInstance() {
        return InnerInstance.instance;
    }

    private Kafka_Producer() {

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

    }

    private Producer<String, String> producer;

    // 启动kafka生产者
    public Kafka_Producer start(Configuration configuration) {
        if (configuration.kafka == null || configuration.kafka.trim().isEmpty()) {
            LOGGER.error("没有配置 kafka");
            return this;
        }
        if (this.producer == null) {
            this.props.put("bootstrap.servers", configuration.kafka);
            // key.serializer.class默认为serializer.class
            // 如果topic不存在，则会自动创建，默认replication-factor为1，partitions为0
            // 创建producer
            this.producer = new KafkaProducer<>(props);


        }
        return this;
    }

    // 关闭一个kafka生产者
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }

    // 发送消息 如果生产者没有初始化只写一次日志
    public void send(String topic, String key, String message) {
        if (this.producer != null) {
            try {
                this.producer.send(new ProducerRecord<String, String>(topic, key, message));
            } catch (Exception e) {
                LOGGER.warn("kafka 发送失败", e);
            }
        } else {
            Logger_Once.warn("kafka未初始化");
        }
    }
}
