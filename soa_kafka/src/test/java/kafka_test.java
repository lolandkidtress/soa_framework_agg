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
        properties.put("zookeeper", "localhost:2181");
        properties.put("kafka","localhost:9092");

        Configuration configuration = null;
        try{
            configuration = Configuration.getInstance().initialization(properties);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("初始化Config异常");
        }

        if(configuration!=null){
            Kafka_Consumer kafka_Consumer = new Kafka_Consumer();

            kafka_Consumer.consume(configuration, "12112312", "largest", 2, "topic", MsgCosum.class);

            System.out.println("start_producer");
            Kafka_Producer.getInstance().start(configuration);
            int i=0;

            while(true){
                i++;
                Kafka_Producer.getInstance().send("topic","key",String.valueOf(i));
                System.out.println("写入"+i);
                TimeUnit.SECONDS.sleep(1);
            }
        }

        Thread.currentThread().join();

    }






}
