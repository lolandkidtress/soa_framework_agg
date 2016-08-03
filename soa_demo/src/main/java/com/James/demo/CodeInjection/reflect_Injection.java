package com.James.demo.CodeInjection;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


/**
 * Created by James on 16/7/21.
 */
public class reflect_Injection {

  final static String class_name = "com.James.demo.CodeInjection.reflect_Injection";
  final static String method_name = "buildString";

  public String buildString(int length) throws Exception{
    String result = "";
    for (int i = 0; i < length; i++) {
      result += (char)(i%26 + 'a');
      TimeUnit.SECONDS.sleep(1);
      System.out.println(result);
    }
    return result;
  }

  public void reflect(){
    try{
      Class clz = Class.forName(class_name);
      Object ob = clz.newInstance();
      Method[] methods = ob.getClass().getMethods();
      for(Method method : methods){
        if(method_name.equals(method.getName()) ){
          System.out.println(method.getName());
          method.invoke(ob, 2);
        }
      }

    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    reflect_Injection injection = new reflect_Injection();
    injection.reflect();

  }
}
