package com.James.Invoker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Model.SharedProvider;
import com.James.NettyAvroRpcClient.avroRpcClient;
import com.James.avroProto.Message;
import com.James.basic.Enum.Code;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Parameter;
import com.James.basic.UtilsTools.Return;
import com.James.http_client.OkHttpTools;


/**
 * Created by James on 16/6/2.
 * 服务调用辅助
 */
public enum InvokerHelper {

  INSTANCE;

  private OkHttpTools okHttpTools =  new OkHttpTools();

  private static final Logger LOGGER = LoggerFactory.getLogger(InvokerHelper.class.getName());

  //关注的服务提供者
  private ConcurrentHashMap<String,Invoker> watchedInvokers = new ConcurrentHashMap();

  public Invoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public void setWatchedInvokers(String key,Invoker invoker){
    this.watchedInvokers.put(key,invoker);
  }


  public Return http_call(SharedProvider sharedProvider,Parameter parameter){


    StringBuffer sb = new StringBuffer();
    sb.append(CommonConfig.HTTP_PROTOCOL_PREFIX);
    sb.append(sharedProvider.getIP());

    sb.append(CommonConfig.COLON);
    sb.append(sharedProvider.getHttp_port());
    if(sharedProvider.getHttp_context()!=null&&sharedProvider.getHttp_context().length()>0){
      sb.append(CommonConfig.SLASH);
      sb.append(sharedProvider.getHttp_context());
    }

    sb.append(CommonConfig.SLASH);
    sb.append(sharedProvider.getMethod_name());


    Map<String, String> headers = new HashMap<>();

    if(sharedProvider.getSubmit_mode().equals(CommonConfig.RequestMethod.GET.name())){
      try {
        return Return.create(okHttpTools.do_get(sb.toString(), parameter, headers ));
      }catch(IOException e){
        e.printStackTrace();
        LOGGER.error("调用okhttp get异常",e);
        return Return.FAIL(Code.error.code,Code.error.name());
      }
    }

    if(sharedProvider.getSubmit_mode().equals(CommonConfig.RequestMethod.POST.name())){
      //TODO
    }

    return Return.FAIL(Code.method_notallow.code,Code.method_notallow.name());

  }

  //TODO netty client 连接池
  public Return avro_call(SharedProvider sharedProvider,Parameter parameter) {

    Message message = new Message();
    message.setParam(JsonConvert.toJson(parameter));
    message.setRequestName(sharedProvider.getMethod_name());

    Return rpc_ret = avroRpcClient.call(sharedProvider.getIP(),
        Integer.valueOf(sharedProvider.getRpc_port()),
        message);

    return rpc_ret;
  }

}
