package com.James.Filter.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by James on 16/8/25.
 * 失败降级时使用的配置
 *
 * 在时间窗口内失败次数过多则会进入降级模式
 * 经过降级持续时间后恢复原有功能
 *
 * 返回降级Code和Note
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ratelimitAnnotation {

  String name();
  String policy() default "before";

  //时间窗口 ms
  int allowPeriod() default 5000;
  //时间窗口内允许调用次数
  int allowTimes() default 5000;
  //降级持续时间 ms
  int freezingTime() default 5000;

  //返回的code
  int code() default -1;
  //返回的note
  String note() default "";


}
