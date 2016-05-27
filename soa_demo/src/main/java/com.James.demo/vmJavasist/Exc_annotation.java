package com.James.demo.vmJavasist;

import java.lang.annotation.*;

/**
 * Created by James on 16/5/27.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Exc_annotation {
}