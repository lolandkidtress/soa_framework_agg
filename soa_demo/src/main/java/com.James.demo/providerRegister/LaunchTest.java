package com.James.demo.providerRegister;

import java.util.Properties;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.util.Utf8;

import com.James.Annotation.InputParamAnnotation;
import com.James.Annotation.OutputParamAnnotation;
import com.James.Annotation.descriptionAnnotation;
import com.James.Invoker.Invoker;
import com.James.Provider.providerInstance;
import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;
import com.James.basic.UtilsTools.Parameter;

import UtilsTools.JsonConvert;


/**
 * Created by James on 16/5/30.
 */
public class LaunchTest implements avrpRequestProto {

  @descriptionAnnotation(author = "james",name="avrosend",submit_mode="",protocol="avro",desc="")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2")
  @Override
  public Utf8 send(Message message)
      throws AvroRemoteException {
    System.out.println("取得的参数为:" + message.getParam() );
    return new Utf8("call test");
  }

  //通过http 方式调用
  @descriptionAnnotation(author = "james",name="start",submit_mode= "GET",protocol="http" ,desc="",version = "1.0")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2",type="String")
  public void start(){
      System.out.println("start");
  }

  //重复的name会有报错
  @descriptionAnnotation(author = "james",name="start",submit_mode= "GET",protocol="http" ,desc="",version = "1.0")
  public void duplicatestart(){
    System.out.println("duplicatestart");
  }

  public static void main(String[] args) throws Exception {
    String zkconnect = "172.16.8.97:2181";

    Properties properties = new Properties();
    properties.setProperty("zkConnect",zkconnect);

    providerInstance.getInstance().readConfig(properties).startServer("com.James.demo");
//
    Invoker demoinvoke = Invoker.create("com.James.demo",zkconnect);

    System.out.println(JsonConvert.toJson(demoinvoke.getAvailableProvider("start")));
    System.out.println(JsonConvert.toJson(demoinvoke.getAvailableProvider("avrosend")));

    System.out.println("start 返回:" + demoinvoke.call("start", Parameter.create()));
    System.out.println("avrosend 返回:" + demoinvoke.call("avrosend", Parameter.create()));

//    avroRpcClient client = new avroRpcClient();
//
//    Message message = new Message();
//    message.setParam(new Utf8(("setTo")));
//    message.setRequestName(new Utf8("test"));
//
//    String response = client.sendRequest("127.0.0.1", avroConfig.getDEFAULT_PORT(),message);
//    System.out.println("");
//    InvokerHelper.INSTANCE.init();
//    InvokerHelper.INSTANCE.watchZKDataChange("/com.James.demo");
//    InvokerHelper.INSTANCE.watchZKChildChange("/com.James.demo");
//    Thread.currentThread().join();
  }
}
