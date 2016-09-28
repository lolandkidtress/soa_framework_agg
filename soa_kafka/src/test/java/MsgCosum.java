import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Kafka_Tools.Kafka_Consume_Handle;

import kafka.consumer.ConsumerIterator;
/**
 * Created by James on 16/5/20.
 */
public class MsgCosum implements Kafka_Consume_Handle {
    private static final Log LOGGER = LogFactory.getLog(MsgCosum.class.getName());

    public MsgCosum() {
    }

    @Override
    public void handle_event(ConsumerIterator<String, String> consumerIterator) {
        while(consumerIterator.hasNext()){
            String message= consumerIterator.next().message();
            System.out.println(message);
            if(message.contains("阿不里哈山")){
                System.out.println("########################################################");
                System.out.println("########################################################");
                System.out.println("########################################################");
            }

        }

    }
}
