package com.James.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by James on 16/5/31.
 */
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)

public @interface descriptionAnnotation {
  public String author();

  public String name();

  public String version() default "defaultVersion";

  public String desc();

  public String submit_mode() default "get";

  public String protocol() default "http";


}
