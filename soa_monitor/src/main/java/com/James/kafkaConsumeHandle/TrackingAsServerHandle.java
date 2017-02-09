package com.James.kafkaConsumeHandle;

import com.James.Kafka_Tools.Kafka_Consume_Handle;

import kafka.consumer.ConsumerIterator;


/**
 * Created by James on 16/5/20.
 *
 *
 */
public class TrackingAsServerHandle implements Kafka_Consume_Handle {
    public TrackingAsServerHandle() {
    }

    @Override
    public void handle_event(ConsumerIterator<String, String> consumerIterator) {
        while(consumerIterator.hasNext()){
            String message= consumerIterator.next().message();
            System.out.println("TrackingAsServerHandle写入kafka的trackingChain:" + message);
        }

    }
}
