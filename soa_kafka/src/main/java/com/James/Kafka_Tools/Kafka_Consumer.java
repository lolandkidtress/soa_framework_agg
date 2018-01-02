package com.James.Kafka_Tools;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;

import com.James.kafka_Config.Configuration;

import static java.time.temporal.ChronoUnit.MINUTES;





/**
 * Created by James on 16/5/20.
 * kafka消费者
 */
public class Kafka_Consumer {
    private static final Logger LOGGER = LogManager.getLogger(Kafka_Consumer.class.getName());

    private static class InnerInstance {
        public static final Kafka_Consumer instance = new Kafka_Consumer();
    }

    public static Kafka_Consumer getInstance() {
        return InnerInstance.instance;
    }

    //topic和消费实例对应关系
    private ConcurrentHashMap<String,KafkaConsumer<String, Object>> topicMap = new ConcurrentHashMap<>();

    public static ExecutorService executors = Executors.newCachedThreadPool((r) -> {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setDaemon(true);
        return thread;
    });

    public Kafka_Consumer(){
    }

    //初始化实例
    public void init(String group,String clientId,String kafka,String topic){
        if(topicMap.containsKey(topic)){
            //已存在,不更新
            LOGGER.info("消费端已存在,不更新");
        }else{
            try{
                Properties props = new Properties();
                props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
                props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka);
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
                //1.0.0
                //props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,"read_committed");
                //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
                props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
                props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");

                KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(props);
                LOGGER.info("消费端初始化");
                topicMap.put(topic,consumer);
            }catch(Exception e){
                e.printStackTrace();
                LOGGER.error("消费端初始化异常");
            }
        }
    }

    //初始化实例
    public void init(Configuration configuration,String topic){
        if(topicMap.containsKey(topic)){
            //已存在,不更新
            LOGGER.info("消费端已存在,不更新");
        }else{
            try{
                Properties props = new Properties();
                props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.group);
                props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.clientId);
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafka);
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

                //1.0.0
                //props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,"read_committed");
                props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
                props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");

                KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(props);
                LOGGER.info("消费端初始化");
                topicMap.put(topic,consumer);
            }catch(Exception e){
                e.printStackTrace();
                LOGGER.error("消费端初始化异常");
            }
        }
    }

    /**
     * @param topic
     *            topic
     * @param clazz
     *            处理逻辑
     */
    //从上次提交的offset开始消费
    public void consume(String topic,Class<? extends Kafka_Consume_Handle> clazz) {
        KafkaConsumer<String, Object> consumer = topicMap.get(topic);
        if(consumer == null){
            LOGGER.error("消费端没有初始化");
        }else{
            consumer.subscribe(Collections.singletonList(topic));
            LOGGER.info("消费端开始消费"+topic);
            Thread thread = new Thread(() -> {
                Kafka_Consume_Handle newInstance = null;
                while (true) {
                    ConsumerRecords<String, Object> records = consumer.poll(1000);
                    for (ConsumerRecord<String, Object> record : records) {
                        //System.out.println("record:" + record.value());
                        try {
                            newInstance = clazz.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                            LOGGER.error("#实例化Consume_Handle失败:", e);
                        }
                        newInstance.handle_event(record);
                    }
                }

            });
            thread.setDaemon(true);
            thread.start();
            }
        }

    @Deprecated
    //老版本兼容
    public void consume(Configuration configuration, String group,String offset,int concurrent, String topic, Class<? extends Kafka_Consume_Handle> clazz) {
        KafkaConsumer<String, Object> consumer = topicMap.get(topic);
        if(consumer == null){
            init(configuration,topic);
            consume(topic, clazz);
        }else{
            consume(topic, clazz);
        }
    }

    //从现存的最小的offset开始取得数据
    public void consumeFromBegining(String topic,Class<? extends Kafka_Consume_Handle> clazz) {
        try{
            KafkaConsumer<String, Object> consumer = topicMap.get(topic);
            if(consumer == null){
                LOGGER.error("消费端没有初始化");
            }else{
                consumer.subscribe(Collections.singletonList(topic));

                Thread thread = new Thread(() -> {
                    Kafka_Consume_Handle newInstance = null;
                    while (true) {
                        ConsumerRecords<String, Object> records = consumer.poll(1000);
                        //在每一分区上重置offset
                        Set<TopicPartition> assignments = consumer.assignment();
                        assignments.forEach(topicPartition ->
                            consumer.seekToBeginning(
                                Collections.singletonList(topicPartition)));

                        for (ConsumerRecord<String, Object> record : records) {
                            //System.out.println("record:" + record.value());
                            try {
                                newInstance = clazz.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                                LOGGER.error("#实例化Consume_Handle失败:", e);
                            }
                            newInstance.handle_event(record);
                        }
                    }

                });
                thread.setDaemon(true);
                thread.start();

            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    //从现存的最小的offset开始取得数据
    public void consumeFromLastest(String topic,Class<? extends Kafka_Consume_Handle> clazz) {
        try{
            KafkaConsumer<String, Object> consumer = topicMap.get(topic);
            if(consumer == null){
                LOGGER.error("消费端没有初始化");
            }else{
                consumer.subscribe(Collections.singletonList(topic));

                Thread thread = new Thread(() -> {
                    Kafka_Consume_Handle newInstance = null;
                    while (true) {
                        ConsumerRecords<String, Object> records = consumer.poll(1000);
                        //在每一分区上重置offset
                        Set<TopicPartition> assignments = consumer.assignment();
                        assignments.forEach(topicPartition ->
                            consumer.seekToEnd(Collections.singletonList(topicPartition)));

                        for (ConsumerRecord<String, Object> record : records) {
                            //System.out.println("record:" + record.value());
                            try {
                                newInstance = clazz.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                                LOGGER.error("#实例化Consume_Handle失败:", e);
                            }
                            newInstance.handle_event(record);
                        }
                    }

                });
                thread.setDaemon(true);
                thread.start();

            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //从过去某个时间段开始
    public void consumeFromTimes(String topic,int minute,Class<? extends Kafka_Consume_Handle> clazz) {
        try{
            KafkaConsumer<String, Object> consumer = topicMap.get(topic);
            if(consumer == null){
                LOGGER.error("消费端没有初始化");
            }else{
                consumer.subscribe(Collections.singletonList(topic));

                Thread thread = new Thread(() -> {
                    Kafka_Consume_Handle newInstance = null;
                    while (true) {
                        ConsumerRecords<String, Object> records = consumer.poll(1000);
                        Set<TopicPartition> assignments = consumer.assignment();
                        Map<TopicPartition, Long> query = new HashMap<>();
                        for (TopicPartition topicPartition : assignments) {
                            //在每一分区上寻找对应的offset
                            query.put(
                                topicPartition,
                                Instant.now().minus(minute, MINUTES).toEpochMilli());
                        }
                        Map<TopicPartition, OffsetAndTimestamp> result = consumer.offsetsForTimes(query);
                        //根据找到的offset修改,没有则从最新的offset开始
                        result.entrySet()
                            .stream()
                            .forEach(entry ->
                                consumer.seek(
                                    entry.getKey(),
                                    Optional.ofNullable(entry.getValue())
                                        .map(OffsetAndTimestamp::offset)
                                        .orElse(new Long(Long.MAX_VALUE)))
                            );


                        for (ConsumerRecord<String, Object> record : records) {
                            //System.out.println("record:" + record.value());
                            try {
                                newInstance = clazz.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                                LOGGER.error("#实例化Consume_Handle失败:", e);
                            }
                            newInstance.handle_event(record);
                        }
                    }

                });
                thread.setDaemon(true);
                thread.start();

            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        org.apache.log4j.BasicConfigurator.configure();
        Properties properties = new Properties();
        properties.put("kafka","localhost:9092");
        properties.put("clientId","testclient2");
        properties.put("group","testclient2");

        Configuration configuration = null;
        try{
            configuration = Configuration.getInstance().initialization(properties);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("初始化Config异常");
        }
        try{
            Properties props = new Properties();
            props.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.group);
            props.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.clientId);
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafka);
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

            //1.0.0
            //props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG,"read_committed");
            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
            props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

            KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(props);
            LOGGER.info("消费端初始化");
            consumer.subscribe(Collections.singletonList("topic_metric_tracking"));
            LOGGER.info("消费端开始消费" + "topic_metric_tracking");
            Thread thread = new Thread(() -> {
                while (true) {
                    ConsumerRecords<String, Object> records = consumer.poll(1000);

                    for (ConsumerRecord<String, Object> record : records) {
                        System.out.println("time:"+System.currentTimeMillis()+",partition:"+record.partition()+",record:" + record.value());
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
            Thread.currentThread().join();
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("消费端初始化异常");
        }



    }
}
