package com.sanwenyukaochi.security.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    String tenantId() default "tenantId";
    String userid() default "createdBy";
}