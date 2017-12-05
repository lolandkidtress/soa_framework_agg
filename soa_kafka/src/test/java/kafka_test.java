import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

import com.James.Kafka_Tools.Kafka_Consumer;
import com.James.Kafka_Tools.Kafka_Producer;
import com.James.kafka_Config.Configuration;

/**
 * Created by James on 16/5/20.
 */
public class kafka_test {

    private static final Logger LOGGER = LogManager.getLogger(kafka_test.class.getName());

    public static void main(String[] args) throws Exception{
        //屏蔽掉zk和kafka自身的日志输出
        //TODO 自定义配置Appender
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());

        Properties properties = new Properties();
        //properties.put("zookeeper", "10.81.23.103:2181,10.81.23.104:2181,10.81.23.105:2181");
        properties.put("kafka","10.81.23.100:9092,10.81.23.101:9092,10.81.23.102:9092");
        properties.put("clientId","testclient");
        properties.put("group","testclient");

        Configuration configuration = null;
        try{
            configuration = Configuration.getInstance().initialization(properties);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("初始化Config异常");
        }

        String topic = "IP_input";
        if(configuration!=null){
            Kafka_Consumer.getInstance().init(configuration, "topic_ip_aggre_www");
            //Kafka_Consumer.getInstance().consume("topic_short_url_mysql_local", MsgCosum.class);
            //Kafka_Consumer.getInstance().consumeFromBegining("topic_v1_t2", MsgCosum.class);
            Kafka_Consumer.getInstance().consumeFromLastest("topic_short_url_mysql_www", MsgCosum.class);
            //Kafka_Consumer.getInstance().consumeFromTimes("topic_short_url_mysql", 5, MsgCosum.class);
            //Kafka_Consumer.getInstance().consume(configuration,"grouptopic_v1_test","0",2, "topic_short_url_mysql", MsgCosum.class);

            //System.out.println("start_producer");
            Kafka_Producer.getInstance().init(configuration,topic);
            int i=0;


            List<String> CodeList = new ArrayList<>();
            CodeList.add("a");
            CodeList.add("b");
            CodeList.add("c");
            CodeList.add("d");
//
//            Kafka_Producer.getInstance().send(topic,"a", null);
//            Kafka_Producer.getInstance().send(topic,"b", null);
//            Kafka_Producer.getInstance().send(topic,"c", null);
//            Kafka_Producer.getInstance().send(topic,"d", null);
            while(true){
                i++;
                int random = (int) (Math.random() * CodeList.size());
                String key = CodeList.get(random);
                //Kafka_Producer.getInstance().send(topic,key, "127.0.0.".concat(String.valueOf(i % 4)));
                //System.out.println("写入" + i);
                TimeUnit.SECONDS.sleep(1);
            }
        }

        Thread.currentThread().join();

    }






}
