package com.James;

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

  @descriptionAnnotation(author = "james",name="start",submit_mode= "GET",protocol="http" ,desc="",version = "1.0")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2",type="String")
  public void start(){
      System.out.println("start");
  }

  @descriptionAnnotation(author = "james",name="send",submit_mode="POST",protocol="avro",desc="")
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
    Invoker invoke2 = Invoker.create("com.James.demo",zkconnect);

    System.out.println(JsonConvert.toJson(invoke2.getAvailableProvider("start")));

    invoke2.call("start", Parameter.create());
//    InvokerHelper.INSTANCE.init();
//    InvokerHelper.INSTANCE.watchZKDataChange("/com.James.demo");
//    InvokerHelper.INSTANCE.watchZKChildChange("/com.James.demo");
    Thread.currentThread().join();
  }
}
