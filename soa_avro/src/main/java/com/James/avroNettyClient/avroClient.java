package com.James.avroNettyClient;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;


/**
 * Created by James on 16/6/26.
 * avro 客户端
 * TODO 连接池
 * TODO 异步化
 */
public class avroClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(avroClient.class.getName());

  private NettyTransceiver initClient(String hostname,int port){
    try{
      NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(hostname,port));
      return client;
    }catch (IOException e){
      e.printStackTrace();
      LOGGER.error("初始化netty client 异常",e);
      return null;
    }

  }

  private avrpRequestProto bindProxy(NettyTransceiver client){
   try{
     avrpRequestProto proxy = (avrpRequestProto) SpecificRequestor.getClient(avrpRequestProto.class, client);

     return proxy;
   }catch(IOException e) {
     e.printStackTrace();
     LOGGER.error("绑定request接口异常",e);
     return null;
   }

  }


  public String sendRequest(String hostname,int port,Message message){
    try{

      return bindProxy(initClient(hostname,port)).send(message).toString();
    }catch(AvroRemoteException e){
      e.printStackTrace();
      LOGGER.error("调用" + message.getRequestName() + "接口异常",e);
      return null;
    }

  }
}
