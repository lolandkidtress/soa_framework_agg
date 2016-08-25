package com.James.avroNettyServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.avro.util.Utf8;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;
import com.James.avroServiceRegist.avroRequestHandleRegister;
import com.James.basic.Enum.Code;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 16/6/26.
 * AVRO服务端
 *
 */

public class avroServer {

  private static final Log LOGGER = LogFactory.getLog(avroServer.class.getName());

  public static class avrpRequestProtoImpl implements avrpRequestProto {
    // in this simple example just return details of the message
    public Utf8 send(Message message) {

      if(message.getRequestName()==null||message.getRequestName().length()<=0){
        LOGGER.error("RequestName参数不正确");
        Return ret = Return.FAIL(Code.parameters_incorrect.code,Code.parameters_incorrect.name());
        return new Utf8(ret.toJson());
      }
      LOGGER.info("接收到" + message.getRequestName() + "请求");
      String response ="";

      avrpRequestProto avrpRequestProto =  avroRequestHandleRegister.INSTANCE.getRequestHandle(
          message.getRequestName().toString());
      if(avrpRequestProto==null){
        Return ret = Return.FAIL(Code.service_notfound.code,Code.service_notfound.name());
        return new Utf8(ret.toJson());
      }
      try{
        response = avrpRequestProto.send(message).toString();
      }catch(AvroRemoteException e){
        e.printStackTrace();
        LOGGER.error("转发"+message.getRequestName() + "服务异常",e);
        Return ret = Return.FAIL(Code.error.code,Code.error.name());
        return new Utf8(ret.toJson());
      }
      Return ret = Return.SUCCESS(Code.success.code,Code.success.name()).put("data",response);
      return new Utf8(ret.toJson());
    }
  }

  private static Server server;

  public static void startServer() throws IOException {
    startServer(Integer.valueOf(CommonConfig.defaultAvroPort));

  }

  public static void startServer(int port) throws IOException {
    server = new NettyServer(new SpecificResponder(avrpRequestProto.class, new avrpRequestProtoImpl()), new InetSocketAddress(port));
    LOGGER.info("avro Netty Server Started @ " + port);
  }


}
