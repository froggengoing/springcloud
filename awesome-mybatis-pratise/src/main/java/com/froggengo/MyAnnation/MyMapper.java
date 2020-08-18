package com.froggengo.MyAnnation;

import org.apache.ibatis.annotations.Mapper;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface MyMapper {
}
