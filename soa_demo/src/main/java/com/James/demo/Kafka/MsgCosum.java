package com.James.demo.Kafka;

import com.James.Kafka_Tools.Kafka_Consume_Handle;

import kafka.consumer.ConsumerIterator;


/**
 * Created by James on 16/5/20.
 *
 *
 */
public class MsgCosum implements Kafka_Consume_Handle {
    public MsgCosum() {
    }

    @Override
    public void handle_event(ConsumerIterator<String, String> consumerIterator) {
        while(consumerIterator.hasNext()){
            String message= consumerIterator.next().message();
            System.out.println(message);
        }

    }
}
