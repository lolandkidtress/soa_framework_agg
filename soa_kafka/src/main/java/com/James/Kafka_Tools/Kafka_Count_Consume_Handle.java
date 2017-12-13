package com.James.Kafka_Tools;

import org.apache.kafka.clients.consumer.ConsumerRecord;


/**
 * Created by James on 2017/12/7.
 */
public interface Kafka_Count_Consume_Handle {
  public abstract void handle_event(ConsumerRecord<String, Long> it);
}
