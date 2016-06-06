package com.James;

import com.James.Annotation.InputParamAnnotation;
import com.James.Annotation.OutputParamAnnotation;
import com.James.Annotation.descriptionAnnotation;
import com.James.Invoker.Invoker;
import com.James.Provider.providerInstance;


/**
 * Created by James on 16/5/30.
 */
public class Launch {

  @descriptionAnnotation(author = "james",name="start",submit_mode="get",protocol="http",port = "8080" ,desc="",version = "1.0")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2",type="String")
  public void start(){
    String zkconnect = "172.16.8.97:2181";
    zkInstance.INSTANCE.init(zkconnect);
    providerInstance.getInstance().startServer("com.James.soa_discovery");
  }

  @descriptionAnnotation(author = "james",name="end",submit_mode="post",protocol="thrift",port = "8080" ,desc="")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2")
  public void end(){
    String zkconnect = "172.16.8.97:2181";
    zkInstance.INSTANCE.init(zkconnect);
    providerInstance.getInstance().startServer("com.James.soa_discovery");
  }

  public static void main(String[] args) throws Exception {
    new Launch().start();

    Invoker invoke = Invoker.create("com.James.soa_discovery");
    Thread.currentThread().join();
  }
}
