package com.James.demo.providerRegister;

import java.util.Properties;

import com.James.Annotation.InputParamAnnotation;
import com.James.Annotation.OutputParamAnnotation;
import com.James.Annotation.descriptionAnnotation;
import com.James.Invoker.Invoker;
import com.James.Provider.providerInstance;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Parameter;


/**
 * Created by James on 16/5/30.
 */
public class LaunchTest {


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

  @descriptionAnnotation(author = "james",name="avrosend",submit_mode="",protocol="avro",desc="")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2")
  public void send(){
    System.out.println("end");

  }

  public static void main(String[] args) throws Exception {
    String zkconnect = "172.16.8.97:2181";

    Properties properties = new Properties();
    properties.setProperty("zkConnect",zkconnect);

    providerInstance.getInstance().readConfig(properties).startServer("com.James.demo");

//    providerInstance.getInstance().readConfig(properties).startServer("com.James.soa_discovery");

//    Invoker invoke = Invoker.create("com.James.soa_discovery",zkconnect);
//
    Invoker demoinvoke = Invoker.create("com.James.demo",zkconnect);

    System.out.println(JsonConvert.toJson(demoinvoke.getAvailableProvider("start")));
    System.out.println(JsonConvert.toJson(demoinvoke.getAvailableProvider("avrosend")));

    demoinvoke.call("start", Parameter.create());
//    InvokerHelper.INSTANCE.init();
//    InvokerHelper.INSTANCE.watchZKDataChange("/com.James.demo");
//    InvokerHelper.INSTANCE.watchZKChildChange("/com.James.demo");
    Thread.currentThread().join();
  }
}
