package com.James.avroNettyClientConnect;

import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;
import com.James.basic.Enum.BasicCode;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 2017/1/23.
 */
public class avroNettyClientConnection {

  private static final Log logger = LogFactory.getLog(avroNettyClientConnection.class.getName());

  //avro client对象
  private avrpRequestProto proxy = null;

  private String host;  //ip
  private int port;    //端口
  private String name;
  private NettyTransceiver client;
  private long lastInvokeTime;//上次调用时间


  public avroNettyClientConnection(String host,int port){

    this.host=host;
    this.port=port;
    //时间戳+线程名
    this.name = String.valueOf(System.currentTimeMillis()).concat(Thread.currentThread().getName());


    try {
      this.client = new NettyTransceiver(new InetSocketAddress(host, port));
      this.proxy = (avrpRequestProto) SpecificRequestor.getClient(avrpRequestProto.class, this.client);
      logger.debug("avro client初始化成功");

    }catch(Exception e){
      e.printStackTrace();
      logger.error("初始化avro client异常",e);

    }
  }

  public Return call(Message message){
    try{
      String response = proxy.send(message).toString();
      return JsonConvert.toObject(response, Return.class);

    }catch(Exception e){
      e.printStackTrace();
      logger.error("调用avro client异常",e);
      return Return.FAIL(BasicCode.error.code, BasicCode.error.name());
    }
  }

  public void destroyConn() {
    try{
      if (this.client != null && this.client.isConnected()) {
        this.client.close();
      }
    }catch(Exception e){
      e.printStackTrace();
      logger.error("关闭avro client异常",e);
    }

  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
