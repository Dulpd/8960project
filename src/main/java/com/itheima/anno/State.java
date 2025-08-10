package com.itheima.anno;

import com.itheima.validation.StateValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = {StateValidation.class})//指定提供校验规则的类
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

public @interface State {
    //提供校验失败后的提示信息
    String message() default "state只能是已发布或者草稿！！";

    //z指定分组
    Class<?>[] groups() default {};

    //负载 获取到state的注解的附加信息
    Class<? extends Payload>[] payload() default {};
}
