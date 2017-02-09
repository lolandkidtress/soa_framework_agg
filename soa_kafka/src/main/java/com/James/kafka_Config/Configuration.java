package com.James.kafka_Config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.util.Properties;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.James.basic.UtilsTools.NativePath;

/**
 * Created by James on 16/5/20.
 */
public class Configuration {
    private final static Logger logger = LoggerFactory.getLogger(Configuration.class.getName());

    private static class InnerInstance {
        public static final Configuration instance = new Configuration();
    }

    public static Configuration getInstance() {
        return InnerInstance.instance;
    }

    private Configuration() {
    }

    public final static ZoneId zoneid = ZoneId.of("GMT+08:00");
    public final static Charset charset = StandardCharsets.UTF_8;

    public String zookeeper;
    public String kafka;

    // ///////////////////////////////////// initialization //////////////////////////////////////////
    //通过配置文件初始化
    public Configuration initialization(String config_path) throws IOException, URISyntaxException {
        Properties properties = new Properties();
        try (InputStream resourceAsStream = Files.newInputStream(NativePath.get(config_path), StandardOpenOption.READ); //
             InputStreamReader inputstreamreader = new InputStreamReader(resourceAsStream, Configuration.charset);) {
            properties.load(inputstreamreader);
        }
        return initialization(properties);
    }

    //通过属性初始化
    public Configuration initialization(Properties properties) throws IOException, URISyntaxException {
        logger.info("#读取配置:");
        properties.forEach((k, v) -> {
            logger.info(k + "=" + v);
        });

        zookeeper = properties.getProperty("zookeeper");
        if (zookeeper == null || zookeeper.trim().isEmpty()) {
            logger.error("zookeeper配置不能为空:zookeeper");
            System.exit(-1);
        }
        kafka = properties.getProperty("kafka");
        if (kafka == null || kafka.trim().isEmpty()) {
            logger.warn("kafka配置为空:kafka 调用链/日志等功能将不可用");
        }

//
        return this;
    }
}
