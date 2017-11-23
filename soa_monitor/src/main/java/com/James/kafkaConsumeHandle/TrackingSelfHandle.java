package com.James.kafkaConsumeHandle;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.James.Kafka_Tools.Kafka_Consume_Handle;
import com.James.MonitorInstance;


/**
 * Created by James on 16/5/20.
 *
 *
 */
public class TrackingSelfHandle implements Kafka_Consume_Handle {
    public TrackingSelfHandle() {
    }

    @Override
    public void handle_event(ConsumerRecord<String, String> consumerIterator) {
        if(consumerIterator.key().equals(MonitorInstance.INSTANCE.getClientID())){
            String message= consumerIterator.value();
            System.out.println("写入kafka的trackingChain:" + message);
        }

    }
}
