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
    public void handle_event(ConsumerRecord<String, Object> consumerIterator) {
        if(consumerIterator.key().equals(MonitorInstance.INSTANCE.getClientID())){
            String message= String.valueOf(consumerIterator.value());
            //TODO 实际业务场景
            System.out.println("写入kafka的trackingChain:" + message);
        }

    }
}
