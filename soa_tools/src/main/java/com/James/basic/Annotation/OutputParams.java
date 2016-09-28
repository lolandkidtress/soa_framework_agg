package com.James.basic.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by James on 16/5/31.
 */
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputParams {
  public OutputParamAnnotation[] value() default {};
}
