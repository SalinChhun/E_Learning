package com.elearning.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomErrorFormat {
    ErrorFormat value() default ErrorFormat.DEFAULT;

    enum ErrorFormat {
        DEFAULT,        // Your standard ApiResponse format
        SIMPLE,         // {code, status} format
        DETAIL,         // {code, detail} format
        NESTED          // {error: {code, message}} format
    }
}