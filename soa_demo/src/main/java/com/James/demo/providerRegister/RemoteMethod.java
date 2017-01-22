package com.James.demo.providerRegister;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.util.Utf8;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.James.Filter.Annotation.degradeAnnotation;
import com.James.Filter.Annotation.ratelimitAnnotation;
import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;
import com.James.basic.Annotation.InputParamAnnotation;
import com.James.basic.Annotation.OutputParamAnnotation;
import com.James.basic.Annotation.descriptionAnnotation;
import com.James.basic.Enum.Code;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 16/5/30.
 * 测试用的方法
 */
@RestController
public class RemoteMethod implements avrpRequestProto {

  //avro方法
  @descriptionAnnotation(author = "james",name="avrosend",submit_mode="",protocol="avro",desc="")
  @Override
  public Utf8 send(Message message)
      throws AvroRemoteException {
    System.out.println("avro取得的参数为:" + message.getParam() );
    return new Utf8("avrosend");
  }

  //通过http 方式调用 ,需要自行嵌入spring等容器
  //RequestMapping和descriptionAnnotation的name应该一致
  @RequestMapping(value = "/start", method = RequestMethod.GET)
  @descriptionAnnotation(author = "james",name="start",submit_mode= "GET",protocol="http" ,desc="",version = "1.0")
  @InputParamAnnotation(name ="param1",describe = "参数1")
  @InputParamAnnotation(name ="param2",describe = "参数2")
  @OutputParamAnnotation(name ="outparam",describe = "参数2",type="String")
  public Return start(String param1,String param2){

      System.out.println("调用到start:参数1为"+param1+",参数2为:"+param2);
      return Return.SUCCESS(200,"调用start接口返回成功");
  }

  //5秒只能通过一次
  @RequestMapping(value = "/ratelimitCall", method = RequestMethod.GET)
  @descriptionAnnotation(author = "james",name="ratelimitCall",submit_mode= "GET",protocol="http" ,desc="",version = "1.0")
  @ratelimitAnnotation(name="ratelimitCall",policy = "before",allowPeriod=5000 ,allowTimes = 2)
  public Return ratelimitCall(){
    return Return.SUCCESS(Code.success.code, "invoke ratelimitCall");
  }

  //5秒内失败1次就降级
  @RequestMapping(value = "/degradeCall", method = RequestMethod.GET)
  @descriptionAnnotation(author = "james",name="degradeCall",submit_mode= "GET",protocol="http" ,desc="",version = "1.0")
  @degradeAnnotation(name="degradeCall",policy = "before",allowPeriod=5000 ,allowTimes = 1)
  public Return degradeCall(){
    return Return.FAIL(Code.error.code, "调用degradeCall返回非200");
  }

  //重复的name会有报错
  @descriptionAnnotation(author = "james",name="start",submit_mode= "GET",protocol="http" ,desc="",version = "1.0")
  public void duplicaTest(){
    System.out.println("duplicatestart");
  }

}
