import com.James.Kafka_Tools.Kafka_Consume_Handle;
import kafka.consumer.ConsumerIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by James on 16/5/20.
 */
public class MsgCosum implements Kafka_Consume_Handle {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgCosum.class.getName());

    public MsgCosum() {
    }

//    public void run(int numThreads, Kafka_Consumer kafka_Consumer) {
//        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//        topicCountMap.put(topic, new Integer(numThreads));
//
//        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
//        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
//        // now launch all the threads
//        executor = Executors.newFixedThreadPool(numThreads);
//        // now create an object to consume the messages
//        int threadNumber = 0;
//        for (final KafkaStream<byte[], byte[]> stream : streams) {
//            executor.submit(new ConsumerMsgTask(stream, threadNumber,topic,Kafka_Consume_Handle,env));
//            threadNumber++;
//        }
//    }

    @Override
    public void handle_event(ConsumerIterator<String, String> consumerIterator) {
        while(consumerIterator.hasNext()){
            String message= consumerIterator.next().message();
//            if(message.contains("DataApi")){
//                System.out.println(message);
//            }
            System.out.println(message);
        }

//        System.out.println(it.key() + ":"+ it.value());
//        LOGGER.info(it.key());
//        LOGGER.info(it.value());
    }
}
