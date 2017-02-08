package com.James.avroNettyServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

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
import com.James.basic.Model.trackingChain;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Return;
import com.James.basic.UtilsTools.ThreadLocalCache;


/**
 * Created by James on 16/6/26.
 * AVRO服务端
 *
 */

public class avroServer {

  private static final Log logger = LogFactory.getLog(avroServer.class.getName());

  public static class avrpRequestProtoImpl implements avrpRequestProto {
    // in this simple example just return details of the message
    public Utf8 send(Message message) {

      if(message.getRequestName()==null||message.getRequestName().length()<=0){
        logger.error("RequestName参数不正确");
        Return ret = Return.FAIL(Code.parameters_incorrect.code,Code.parameters_incorrect.name());
        return new Utf8(ret.toJson());
      }

      try{
        Map<String,String> par = JsonConvert.toObject(message.getParam().toString(), HashMap.class);
        trackingChain tc = ThreadLocalCache.getCallchain().get();
        if(tc==null){
          tc=new trackingChain(par.get(CommonConfig.s_trackingID));
        }
        tc.setClientID(CommonConfig.clientID);

        ThreadLocalCache.setCallchain(tc);
      }catch(Exception e){
        Return ret = Return.FAIL(Code.parameters_incorrect.code,Code.parameters_incorrect.name());
        return new Utf8(ret.toJson());
      }

      String response ="";

      avrpRequestProto avrpRequestProto =  avroRequestHandleRegister.INSTANCE.getRequestHandle(
          message.getRequestName().toString());
      if(avrpRequestProto==null){
        Return ret = Return.FAIL(Code.service_not_found.code,Code.service_not_found.name());
        return new Utf8(ret.toJson());
      }
      try{
        response = avrpRequestProto.send(message).toString();
      }catch(AvroRemoteException e){
        e.printStackTrace();
        logger.error("转发"+message.getRequestName() + "服务异常",e);
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
    logger.info("avro Netty Server Started @ " + port);
  }


}
