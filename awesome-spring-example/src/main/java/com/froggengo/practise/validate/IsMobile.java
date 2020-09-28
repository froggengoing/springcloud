package com.froggengo.practise.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy={IsMobileValidator.class})
public @interface IsMobile {
    boolean required() default true;
    String message() default "手机号码格式错误";
    //IsMobile contains Constraint annotation, but does not contain a groups parameter.
    //必须有groups方法，否则报错
    Class<?>[] groups() default {};
    //IsMobile contains Constraint annotation, but does not contain a payload parameter.
    //也报错，，所以这几个方法是不是一定的
    Class<? extends Payload>[] payload() default {};
}
