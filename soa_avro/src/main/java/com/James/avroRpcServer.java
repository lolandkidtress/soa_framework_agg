package com.James;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.reflect.ReflectResponder;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.util.Utf8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;
import com.James.avroServerHandle.avroServerHandle;
import com.James.test.test;


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

  //Impl中转发请求
  public static class avrpRequestProtoImpl implements avrpRequestProto {

    public Utf8 send(Message message) {
      LOGGER.info("接收到" + message.getRequestName() + "请求");
      String ret ="";

      avrpRequestProto avrpRequestProto =  avroServerHandle.INSTANCE.getRegisterServers(
          message.getRequestName().toString());
      if(avrpRequestProto==null){
        return new Utf8("没有服务");
      }
      try{
        ret = avrpRequestProto.send(message).toString();
      }catch(AvroRemoteException e){
        e.printStackTrace();
        LOGGER.error("调用avro接口异常",e);
      }

      return new Utf8(ret);
    }


  }

  private static Server server;

  public avroRpcServer startServer() throws IOException {

    server = new NettyServer(new ReflectResponder(avrpRequestProto.class, new avrpRequestProtoImpl()), new InetSocketAddress(DEFAULT_PORT));

    return this;

  }

  public avroRpcServer startServer(String port) throws IOException {
    server = new NettyServer(new ReflectResponder(avrpRequestProto.class, new avrpRequestProtoImpl()), new InetSocketAddress(Integer.valueOf(port)));

    return this;
  }

  public avroRpcServer addRegisterServers(String handleClass,avrpRequestProto clazz){
    avroServerHandle.INSTANCE.addRegisterServers(handleClass, clazz);
    return this;
  }

  //TEST
  public static void main(String[] args) throws IOException {


    System.out.println("Starting server");

    avroRpcServer.getInstance().startServer().addRegisterServers("test",new test());
    System.out.println("Server started");

    NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(DEFAULT_PORT));
    // client code - attach to the server and send a message
    avrpRequestProto proxy = (avrpRequestProto) SpecificRequestor.getClient(avrpRequestProto.class, client);
    System.out.println("Client built, got proxy");

    // fill in the Message record and send it
    Message message = new Message();
    message.setRequestName("test");
    message.setParam("{\"\":\"\"}");
    System.out.println("Calling proxy.send with message:  " + message.toString());
    System.out.println("Result: " + proxy.send(message));

    // cleanup
    client.close();
    server.close();
  }

}
