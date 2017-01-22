import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Kafka_Tools.Kafka_Consumer;
import com.James.Kafka_Tools.Kafka_Producer;
import com.James.kafka_Config.Configuration;

/**
 * Created by James on 16/5/20.
 */
public class kafka_test {
    private static final Log LOGGER = LogFactory.getLog(kafka_test.class.getName());

    public static void main(String[] args) throws Exception{
        Properties properties = new Properties();
        properties.put("zookeeper", "172.16.10.207:2181/kafka_b");
        properties.put("kafka","172.16.10.203:9092");

        Configuration configuration = null;
        try{
            configuration = Configuration.getInstance().initialization(properties);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("初始化Config异常");
        }

        if(configuration!=null){
            Kafka_Consumer kafka_Consumer = new Kafka_Consumer();

            //kafka_Consumer.consume(configuration, "12112312", "largest", 2, "infogen_topic_tracking", MsgCosum.class);

            System.out.println("start_producer");
            Kafka_Producer.getInstance().start(configuration);
            int i=0;

            while(true){
                i++;
                Kafka_Producer.getInstance().send("infogen_topic_tracking","key",String.valueOf(i));
                System.out.println(1);
                TimeUnit.MINUTES.sleep(1);
            }
        }

        Thread.currentThread().join();

    }






}
