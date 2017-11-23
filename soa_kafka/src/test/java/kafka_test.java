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
        properties.put("zookeeper", "10.81.23.103:2181,10.81.23.104:2181,10.81.23.105:2181");
        properties.put("kafka","10.81.23.100:9092,10.81.23.101:9092,10.81.23.102:9092");
        properties.put("clientId","testclient");

        Configuration configuration = null;
        try{
            configuration = Configuration.getInstance().initialization(properties);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("初始化Config异常");
        }

        if(configuration!=null){
            Kafka_Consumer.getInstance().init(configuration, "grouptopic_v1_test", "topic_v1_t2");
            //Kafka_Consumer.getInstance().consume("topic_v1_t2", MsgCosum.class);
            Kafka_Consumer.getInstance().consumeFromBegining("topic_v1_t2", MsgCosum.class);

            System.out.println("start_producer");
            Kafka_Producer.getInstance().init(configuration);
            int i=0;
//
            while(true){
                i++;
                Kafka_Producer.getInstance().send("topic_v1_t2", "key", "c".concat(String.valueOf(i)));
                //System.out.println("写入" + i);
                TimeUnit.SECONDS.sleep(1);
            }
        }

        Thread.currentThread().join();

    }






}
