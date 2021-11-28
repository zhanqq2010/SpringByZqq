package com.zqq.spring;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)   //注解的生命周期
@Target(ElementType.TYPE)        //注解的作用范围
public @interface Scope {
    public String value() default "";   //默认是singleton     prototype是原型模式
}
