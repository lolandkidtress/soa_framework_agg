package com.James;

import com.James.Annotation.descriptionAnnotation;
import com.James.Provider.providerInstance;


/**
 * Created by James on 16/5/30.
 */
public class Launch {

  @descriptionAnnotation(author = "james",name="start",submit_mode="get",protocol="http",port = "8080" ,desc="",version = "1.0")
  public void start(){
    String zkconnect = "172.16.8.97:2181";
    providerInstance.getInstance().startServer("demo",zkconnect);
  }

  @descriptionAnnotation(author = "james",name="end",submit_mode="post",protocol="thrift",port = "8080" ,desc="",version = "1.0")
  public void end(){
    String zkconnect = "172.16.8.97:2181";
    providerInstance.getInstance().startServer("demo",zkconnect);
  }

  public static void main(String[] args) throws Exception {
    new Launch().start();

    Thread.currentThread().join();
  }
}
