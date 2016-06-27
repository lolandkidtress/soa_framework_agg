package com.James.NettyAvroRpcServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.reflect.ReflectResponder;
import org.apache.avro.util.Utf8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;
import com.James.avroServiceRegist.avroRequestHandleRegister;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 16/6/8.
 */
public class avroRpcServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(avroRpcServer.class.getName());

  //默认netty端口
  public static final int DEFAULT_PORT = 40881;

  private static class InnerInstance {
    public static final avroRpcServer instance = new avroRpcServer();
  }

  public static avroRpcServer getInstance() {
    return InnerInstance.instance;
  }

  public avroRpcServer(){

  }

  //
  //Impl中转发请求
  public static class avrpRequestProtoImpl implements avrpRequestProto {

    public Utf8 send(Message message) {
      LOGGER.info("接收到" + message.getRequestName() + "请求");
      String response ="";

      avrpRequestProto avrpRequestProto =  avroRequestHandleRegister.INSTANCE.getRequestHandle(
          message.getRequestName().toString());
      if(avrpRequestProto==null){
        return new Utf8(Return.FAIL(500,"没有服务").toJson());
      }
      try{
        response = avrpRequestProto.send(message).toString();
        return new Utf8(Return.SUCCESS(200,"调用成功").put("response",response).toJson());
      }catch(AvroRemoteException e){
        e.printStackTrace();
        LOGGER.error("调用avro接口异常",e);
        return new Utf8(Return.FAIL(500,"调用avro接口异常").toJson());
      }


    }


  }

  private static Server server;

  public avroRpcServer startServer() throws IOException {


    server = new NettyServer(new ReflectResponder(avrpRequestProto.class, new avrpRequestProtoImpl()), new InetSocketAddress(DEFAULT_PORT));
    LOGGER.info("avro服务启动@" + DEFAULT_PORT );
    return this;

  }

  public avroRpcServer startServer(String port) throws IOException {
    server = new NettyServer(new ReflectResponder(avrpRequestProto.class, new avrpRequestProtoImpl()), new InetSocketAddress(Integer.valueOf(port)));
    LOGGER.info("avro服务启动@" + port);
    return this;
  }

  public avroRpcServer addRegisterServers(String requestName,avrpRequestProto clazz){
    avroRequestHandleRegister.INSTANCE.addRequestHandle(requestName, clazz);
    return this;
  }


}
