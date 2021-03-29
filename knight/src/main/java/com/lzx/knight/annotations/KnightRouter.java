package com.lzx.knight.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface KnightRouter {
    String scheme() default "";

    String[] path();

    Class[] interceptors() default {};
}
