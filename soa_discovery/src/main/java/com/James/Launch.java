package com.James;

import com.James.Annotation.InputParamAnnotation;
import com.James.Annotation.OutputParamAnnotation;
import com.James.Annotation.descriptionAnnotation;
import com.James.Invoker.Invoker;
import com.James.Provider.providerInstance;
import com.James.basic.UtilsTools.JsonConvert;


/**
 * Created by James on 16/5/30.
 */
public class Launch {

  @descriptionAnnotation(author = "james",name="start",submit_mode="get",protocol="http",port = "8080" ,desc="",version = "1.0")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2",type="String")
  public void start(){
      System.out.println("start");
  }

  @descriptionAnnotation(author = "james",name="end",submit_mode="post",protocol="thrift",port = "8080" ,desc="")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2")
  public void end(){
    System.out.println("end");

  }

  public static void main(String[] args) throws Exception {
    String zkconnect = "172.16.8.97:2181";
    providerInstance.getInstance().initzk(zkconnect).startServer("com.James.demo");

    providerInstance.getInstance().initzk(zkconnect).startServer("com.James.soa_discovery");

    Invoker invoke = Invoker.create("com.James.soa_discovery",zkconnect);
//
    Invoker invoke2 = Invoker.create("com.James.demo",zkconnect);

    System.out.println(JsonConvert.toJson(invoke2.Function("end")));


//    InvokerHelper.INSTANCE.init();
//    InvokerHelper.INSTANCE.watchZKDataChange("/com.James.demo");
//    InvokerHelper.INSTANCE.watchZKChildChange("/com.James.demo");
    Thread.currentThread().join();
  }
}
