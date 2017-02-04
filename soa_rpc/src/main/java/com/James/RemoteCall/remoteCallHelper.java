package com.James.RemoteCall;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Exception.avroConnectionException;
import com.James.avroNettyClientConnect.avroNettyClientConnection;
import com.James.avroNettyClientConnect.avroNettyClientConnectionManager;
import com.James.avroNettyClientConnect.avroNettyClientConnectionPool;
import com.James.avroProto.Message;
import com.James.basic.Enum.Code;
import com.James.basic.Model.sharedNode;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Parameter;
import com.James.basic.UtilsTools.Return;
import com.James.http_client.OkHttpTools;


/**
 * Created by James on 16/7/2.
 */
public class remoteCallHelper {

  private static final Log logger = LogFactory.getLog(remoteCallHelper.class.getName());
  private static OkHttpTools okHttpTools =  new OkHttpTools();

  public static Return http_call(sharedNode sharedNode,Parameter parameter){


    StringBuffer sb = new StringBuffer();
    sb.append(CommonConfig.HTTP_PROTOCOL_PREFIX);
    sb.append(sharedNode.getIP());

    sb.append(CommonConfig.COLON);
    sb.append(sharedNode.getHttp_port());
    if(sharedNode.getHttp_context()!=null&& sharedNode.getHttp_context().length()>0){
      sb.append(CommonConfig.SLASH);
      sb.append(sharedNode.getHttp_context());
    }

    sb.append(CommonConfig.SLASH);
    sb.append(sharedNode.getMethod_name());

    Map<String, String> headers = new HashMap<>();

    if(sharedNode.getSubmit_mode().equals(CommonConfig.RequestMethod.GET.name())){
      try {
        return Return.create(okHttpTools.do_get(sb.toString(), parameter, headers ));
      }catch(IOException e){
        e.printStackTrace();
        logger.error("调用okhttp get异常",e);
        return Return.FAIL(Code.error.code,Code.error.name());
      }
    }
    //TODO post实现
    if(sharedNode.getSubmit_mode().equals(CommonConfig.RequestMethod.POST.name())){

    }
    return Return.FAIL(Code.method_not_support.code,Code.method_not_support.name());

  }

  public static Return avro_call(sharedNode sharedNode,Parameter parameter) {

    Message message = new Message();
    message.setParam(JsonConvert.toJson(parameter));
    message.setRequestName(sharedNode.getMethod_name());

    avroNettyClientConnectionPool cp =
        avroNettyClientConnectionManager.getInstance().getConnectPool(sharedNode.getIP(), sharedNode.getRpc_port());
    try{
      avroNettyClientConnection conn = cp.getConnect();
      Return rpc_ret = conn.call(message);
      cp.releaseConnect(conn);
      return rpc_ret;
    }catch(avroConnectionException e){
      e.printStackTrace();
      logger.error(
          "avroConnectionPool调用异常",e);
      return Return.FAIL(Code.avro_Connection_not_available.code,Code.avro_Connection_not_available.name());
    }

  }
}
