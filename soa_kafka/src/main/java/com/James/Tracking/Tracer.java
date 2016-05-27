package com.James.Tracking;

import java.lang.annotation.*;

/**
 * Created by James on 16/5/23.
 * 用于标记记录的注解
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tracer {

    TracerType type() default TracerType.DB;

    String user_definition() default "";
}
