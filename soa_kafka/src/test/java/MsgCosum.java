import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.James.Kafka_Tools.Kafka_Consume_Handle;
/**
 * Created by James on 16/5/20.
 */
public class MsgCosum implements Kafka_Consume_Handle {
    private static final Log LOGGER = LogFactory.getLog(MsgCosum.class.getName());

    public MsgCosum() {
    }

    @Override
    public void handle_event(ConsumerRecord<String, String> consumerIterator) {
        String message= consumerIterator.value();
        System.out.println("接收到"+message);

    }
}
