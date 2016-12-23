package com.James.Annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by James on 2016/10/20.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldCheck {
    Class check();
    int length() default 32;
    String fieldType() default "string";

}
