package com.James.basic.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by James on 16/5/31.
 */
@Repeatable(OutputParams.class)
@Target({ ElementType.METHOD ,ElementType.TYPE, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputParamAnnotation {
  public String name();

  public String describe();

  public boolean Required() default true;

  public String type() default "String";

  public String default_value() default "";

}
