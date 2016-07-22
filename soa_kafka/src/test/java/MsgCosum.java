import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Kafka_Tools.Kafka_Consume_Handle;

import kafka.consumer.ConsumerIterator;
/**
 * Created by James on 16/5/20.
 */
public class MsgCosum implements Kafka_Consume_Handle {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgCosum.class.getName());

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
