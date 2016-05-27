package com.James.Configuration;

import com.James.Basic.UtilsTools.NativePath;
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
//    public final RegisterNode register_node = new RegisterNode();
//    public final RegisterServer register_server = new RegisterServer();
//    public final ServiceFunctions service_functions = new ServiceFunctions();

    // ////////////////////////////////////////////读取自身配置/////////////////////////////////////////////
    //添加注解上的参数
//    public Configuration add_basic_outparameter(OutParameter basic_outparameter) {
//        for (Function function : service_functions.getFunctions()) {
//            function.getOut_parameters().add(basic_outparameter);
//        }
//        return this;
//    }

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

//        // 延迟启动 http mvc 框架
//        String mapping_path = infogen_properties.getProperty("infogen.http.spring_mvc.path");
//        String mapping_pattern = infogen_properties.getProperty("infogen.http.spring_mvc.mapping");
//        InfoGen_Spring.config_mvc(mapping_path, mapping_pattern);
//
//        //
//        register_server.setInfogen_version(InfoGen.VERSION);
//        register_server.setName(infogen_properties.getProperty("infogen.name"));
//        register_server.setDescribe(infogen_properties.getProperty("infogen.describe"));
//        String min_nodes = infogen_properties.getProperty("infogen.min_nodes");
//        register_server.setMin_nodes((min_nodes == null) ? 1 : Integer.valueOf(min_nodes));
//        register_server.setProtocol(infogen_properties.getProperty("infogen.protocol"));
//        register_server.setHttp_proxy(infogen_properties.getProperty("infogen.http.proxy"));
//
//        // server - 自描述
//        if (!register_server.available()) {
//            LOGGER.error("服务配置不能为空:infogen.name");
//            System.exit(-1);
//        }
//
//        // node
//        String localIP = infogen_properties.getProperty("infogen.ip");
//        if (localIP == null || localIP.trim().isEmpty() || !Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)").matcher(localIP).find()) {
//            String ifcfgs = infogen_properties.getProperty("infogen.ifcfgs");
//            localIP = Tool_Core.getLocalIP(Tool_Core.trim((ifcfgs == null || ifcfgs.trim().isEmpty()) ? "eth,wlan" : ifcfgs).split(","));
//        }
//        register_node.setIp(localIP);
//        String net_ip = infogen_properties.getProperty("infogen.net_ip");
//        if (net_ip != null && !net_ip.trim().isEmpty() && Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)").matcher(net_ip).find()) {
//            register_node.setNet_ip(net_ip);
//        }
//
//        register_node.setName(localIP.concat("-" + Clock.system(zoneid).millis()));
//        register_node.setServer_name(register_server.getName());
//        String http_port = infogen_properties.getProperty("infogen.http.port");
//        register_node.setHttp_port((http_port == null) ? null : Integer.valueOf(http_port));
//        String rpc_port = infogen_properties.getProperty("infogen.rpc.port");
//        register_node.setRpc_port((rpc_port == null) ? null : Integer.valueOf(rpc_port));
//        register_node.setHost(System.getProperty("user.name").concat("@").concat(Tool_Core.getHostName()));
//        String ratio = infogen_properties.getProperty("infogen.ratio");
//        register_node.setRatio((ratio == null) ? 10 : Math.max(0, Math.min(10, Integer.valueOf(ratio))));
//        register_node.setHttp_protocol(infogen_properties.getProperty("infogen.http.protocol"));
//        register_node.setHttp_context(infogen_properties.getProperty("infogen.http.context"));
//        register_node.setServer_room(infogen_properties.getProperty("infogen.server_room"));
//        register_node.setTime(new Timestamp(Clock.system(InfoGen_Configuration.zoneid).millis()));
//        String node_version = infogen_properties.getProperty("infogen.node_version");
//        register_node.setNode_version(node_version!=null?node_version:"");
//
//        if (!register_node.available()) {
//            LOGGER.error("节点配置配置不能为空:infogen.name,infogen.ratio,infogen.ip,infogen.http.port或infogen.rpc.port");
//            System.exit(-1);
//        }

        // /////////////////////////////////////////////////////初始化启动配置/////////////////////////////////////////////////////////////////////

//        InfoGen_Self_Description infogen_self_description = InfoGen_Self_Description.getInstance();
//        List<DefaultEntry<Class<? extends Tracer>, Self_Description>> defaultentrys = new ArrayList<>();
//        defaultentrys.add(new DefaultEntry<Class<? extends Tracer>, Self_Description>(RestController.class, new HTTP_Parser()));
//        defaultentrys.add(new DefaultEntry<Class<? extends Tracer>, Self_Description>(RPCController.class, new RPC_Parser()));
//        service_functions.getFunctions().addAll(infogen_self_description.self_description(AOP.getInstance().getClasses(), defaultentrys));
//        service_functions.setServer(register_server);

        return this;
    }
}
