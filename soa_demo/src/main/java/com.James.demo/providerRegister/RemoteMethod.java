package com.James.demo.providerRegister;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.util.Utf8;

import com.James.Annotation.InputParamAnnotation;
import com.James.Annotation.OutputParamAnnotation;
import com.James.Annotation.descriptionAnnotation;
import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;


/**
 * Created by James on 16/5/30.
 * 测试用的方法
 */
public class RemoteMethod implements avrpRequestProto {

  //avro方法
  @descriptionAnnotation(author = "james",name="avrosend",submit_mode="",protocol="avro",desc="")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="param2",describe = "参数2")
  @Override
  public Utf8 send(Message message)
      throws AvroRemoteException {
    System.out.println("取得的参数为:" + message.getParam() );
    return new Utf8("avrosend");
  }

  //通过http 方式调用 ,需要自行嵌入spring等容器
//  @RequestMapping
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

}
