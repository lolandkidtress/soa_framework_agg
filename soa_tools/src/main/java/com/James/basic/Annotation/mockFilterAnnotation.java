package com.James.basic.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by James on 16/8/25.
 * 降级用的注解
 * 降级策略分为
 * 1.Fail_RETURN 调用失败后返回
 * 2.Call_RETURN 调用前返回
 *
 * 在时间窗口内失败次数过多则会进入降级模式
 * 经过降级持续时间后恢复原有功能
 *
 * 返回降级Code
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
public @interface mockFilterAnnotation {
  enum Policy{
    Fail_RETURN,
    Call_RETURN
  }

  String name();

  Policy policy() ;  //Fail_RETURN  或者 Call_RETURN

  //时间窗口 ms
  int allowFailPeriod() default 5000;
  //时间窗口内允许失败的次数
  int allowFailTimes() default 5;
  //降级持续时间 ms
  int freezingTime() default 5000;

  //返回的code
  int code() default 500;
  //返回的note
  String note() default "流量限制";


}
