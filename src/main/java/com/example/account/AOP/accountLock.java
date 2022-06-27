package com.example.account.AOP;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited


public @interface accountLock {
    long tryLockTime() default 5000L;
}
