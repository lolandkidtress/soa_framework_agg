import com.James.kafka_Config.Configuration;
import com.James.Kafka_Tools.Kafka_Consumer;
import com.James.Kafka_Tools.Kafka_Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by James on 16/5/20.
 */
public class kafka_test {
    private static final Logger LOGGER = LoggerFactory.getLogger(kafka_test.class.getName());

    public static void main(String[] args) throws Exception{
        Properties properties = new Properties();
        properties.put("zookeeper", "192.168.202.16:2181/kafka");
//        properties.put("kafka","192.168.202.34:9092,192.168.202.35:9092,192.168.202.36:9092");
        properties.put("kafka","192.168.0.101:9092");

        Configuration configuration = null;
        try{
            configuration = Configuration.getInstance().initialization(properties);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("初始化Config异常");
        }

        if(configuration!=null){
            Kafka_Consumer kafka_Consumer = new Kafka_Consumer();

            kafka_Consumer.consume(configuration, "soa_group_test", "smallest", 2, "xuyufei_test", MsgCosum.class);

            System.out.println("start_producer");
            Kafka_Producer.getInstance().start(configuration);

//            while(true){
//                Kafka_Producer.getInstance().send("soa_test","key",String.valueOf(System.currentTimeMillis()));
//            }
        }

        Thread.currentThread().join();

    }






}
