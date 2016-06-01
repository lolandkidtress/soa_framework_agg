package com.James.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by James on 16/5/31.
 * 多个相同的annotation
 */
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
public @interface InputParams {
  public InputParamAnnotation[] value() default {};
}
