package com.spring.familymoments.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 해당 어노테이션을 controller 메소드 앞에 붙이면 인증 절차를 거치지 않음
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoAuthCheck {
}
