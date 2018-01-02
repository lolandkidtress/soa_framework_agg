import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.log4j.varia.NullAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by James on 2017/12/5.
 */
public class KafkaStreamTest {

  private static final Logger LOGGER = LogManager.getLogger(kafka_test.class.getName());

  public static void main( String[] args ) throws Exception
  {


    org.apache.log4j.BasicConfigurator.configure(new NullAppender());
    Properties props = new Properties();

    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ClickStreamerGet");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());


    KStreamBuilder builder = new KStreamBuilder();

    KafkaStreams streams = new KafkaStreams(builder, props);
    streams.start();

    ReadOnlyKeyValueStore<String, Long> keyValueStore =
        streams.store("Counts", QueryableStoreTypes.keyValueStore());

// Get value by key
    System.out.println("count for code:" + keyValueStore.get("kG"));
  }

}
