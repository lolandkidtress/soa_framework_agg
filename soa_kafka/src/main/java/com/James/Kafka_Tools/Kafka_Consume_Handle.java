package com.James.Kafka_Tools;

import kafka.consumer.ConsumerIterator;

/**
 * Created by James on 16/5/20.
 * 消费事件入口
 */
public interface Kafka_Consume_Handle {
    public abstract void handle_event(ConsumerIterator<String, String> it);
}
