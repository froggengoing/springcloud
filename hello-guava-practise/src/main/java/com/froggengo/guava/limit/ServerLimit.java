package com.froggengo.guava.limit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ServerLimit {
    String description()  default "";
    String key() default "";
    LimitType type() default LimitType.CUSTOMER;
}
