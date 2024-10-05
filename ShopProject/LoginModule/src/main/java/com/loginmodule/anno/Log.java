package com.loginmodule.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//该注解运行时生效
@Target(ElementType.METHOD)//注解运用在方法
public @interface Log {}
