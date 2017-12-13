import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.James.Kafka_Tools.Kafka_Consume_Handle;
/**
 * Created by James on 16/5/20.
 */
public class MsgCosum implements Kafka_Consume_Handle {

    public MsgCosum() {
    }

    @Override
    public void handle_event(ConsumerRecord<String, Object> consumerIterator) {
        String message= (String.valueOf (consumerIterator.value()));
        String key = consumerIterator.key();
        System.out.println("接收到:"+key + ",message:" + message);
    }
}
