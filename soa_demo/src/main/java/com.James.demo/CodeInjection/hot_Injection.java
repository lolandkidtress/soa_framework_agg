package com.James.demo.CodeInjection;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import com.James.soa_agent.HotInjecter;


/**
 * Created by James on 16/7/21.
 */


public class hot_Injection {
  public void inject(){
//        AOP.getInstance().add_advice_method(Execution.class, new InfoGen_AOP_Handle_Execution());
    HotInjecter.getInstance().add_advice_method(Inject_annotation.class, new My_Agent_Handle());
    HotInjecter.getInstance().advice();
  }

  @Inject_annotation
  public String buildString(int length) throws Exception{
    String result = "";
    for (int i = 0; i < length; i++) {
      result += (char)(i%26 + 'a');
      TimeUnit.SECONDS.sleep(1);
      System.out.println(result);
    }
    return result;
  }


  @Target({ ElementType.METHOD })
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Inject_annotation {
  }



}
