package com.James.test;

import org.apache.avro.util.Utf8;

import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;


/**
 * Created by James on 16/6/8.
 */
public class test implements avrpRequestProto {

  private String classname = this.getClass().getName();
  static{
    System.out.println("test init" );

  }

  public test(){

  }

  public Utf8 send(Message message){
    System.out.println(message.getParam() );
    return new Utf8("call test");
  }
}
