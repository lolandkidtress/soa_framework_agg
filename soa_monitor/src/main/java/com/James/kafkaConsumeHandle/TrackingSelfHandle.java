package com.James.kafkaConsumeHandle;

import com.James.Kafka_Tools.Kafka_Consume_Handle;
import com.James.MonitorInstance;

import kafka.consumer.ConsumerIterator;
import kafka.message.MessageAndMetadata;


/**
 * Created by James on 16/5/20.
 *
 *
 */
public class TrackingSelfHandle implements Kafka_Consume_Handle {
    public TrackingSelfHandle() {
    }

    @Override
    public void handle_event(ConsumerIterator<String, String> consumerIterator) {

        while(consumerIterator.hasNext()){
            MessageAndMetadata<String, String> metadata= consumerIterator.next();
            //只接收相同clientID的消息
            if(metadata.key().equals(MonitorInstance.INSTANCE.getClientID())){
                String message= metadata.message();
                System.out.println("写入kafka的trackingChain:" + message);
            }

        }

    }
}
