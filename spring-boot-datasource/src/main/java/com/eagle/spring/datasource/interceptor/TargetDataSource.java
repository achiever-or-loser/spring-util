package com.eagle.spring.datasource.interceptor;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 核心注解，用来切换数据源，可以用来标注在类上、接口上、类方法上、接口方法上
 *
 * @Inherited 注解允许标注的注解标注在类上时其子类可以继承注解，如果标注在非类上在继承作用无效
 * @Author: csc
 * @create: 2023/01/06
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TargetDataSource {
    /**
     * 指定要切换的数据库标识，默认是：default
     *
     * @return 要切换的数据库标识
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 指定要切换的数据库标识，默认是：default
     *
     * @return 要切换的数据库标识
     */
    @AliasFor("value")
    String name() default "";
}

