package com.James.kafka_Config;

import com.James.basic.UtilsTools.NativePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Created by James on 16/5/20.
 */
public class Configuration {
    private final static Logger LOGGER = LoggerFactory.getLogger(Configuration.class.getName());

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
        LOGGER.info("#读取配置:");
        properties.forEach((k, v) -> {
            LOGGER.info(k + "=" + v);
        });

        zookeeper = properties.getProperty("zookeeper");
        if (zookeeper == null || zookeeper.trim().isEmpty()) {
            LOGGER.error("zookeeper配置不能为空:zookeeper");
            System.exit(-1);
        }
        kafka = properties.getProperty("kafka");
        if (kafka == null || kafka.trim().isEmpty()) {
            LOGGER.warn("kafka配置为空:kafka 调用链/日志等功能将不可用");
        }

//
        return this;
    }
}
