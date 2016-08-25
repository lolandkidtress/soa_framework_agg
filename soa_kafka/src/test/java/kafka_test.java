import java.util.Properties;

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
        properties.put("zookeeper", "192.168.202.16:2181/kafka");
        properties.put("kafka","192.168.202.34:9092,192.168.202.35:9092,192.168.202.36:9092");

        Configuration configuration = null;
        try{
            configuration = Configuration.getInstance().initialization(properties);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("初始化Config异常");
        }

        if(configuration!=null){
            Kafka_Consumer kafka_Consumer = new Kafka_Consumer();

            kafka_Consumer.consume(configuration, "soa_group_test", "largest", 2, "soa_test", MsgCosum.class);

            System.out.println("start_producer");
            Kafka_Producer.getInstance().start(configuration);
            int i=0;

            while(true){
               i++;
                Kafka_Producer.getInstance().send("soa_test","key",String.valueOf(i));
            }
        }

        Thread.currentThread().join();

    }






}
