package com.James.kafkaConsumeHandle;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.James.Kafka_Tools.Kafka_Consume_Handle;


/**
 * Created by James on 16/5/20.
 *
 *
 */
public class TrackingAsServerHandle implements Kafka_Consume_Handle {
    public TrackingAsServerHandle() {
    }

    @Override
    public void handle_event(ConsumerRecord<String, String> consumerIterator) {

        String message= consumerIterator.value();
        System.out.println("TrackingAsServerHandle写入kafka的trackingChain:" + message);
    }
}
