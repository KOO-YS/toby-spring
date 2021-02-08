package com.training.spring.aop;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * https://lee-mandu.tistory.com/28
 * https://docs.spring.io/spring-framework/docs/4.0.x/spring-framework-reference/html/aop.html
 */
@Service    // bean 등록을 위해
@Aspect
public class AopConfig {

//    @Pointcut("execution(* *..ServiceImpl.upgrade*(..))")
    @Pointcut("execution(* *..ServiceImpl.upgrade*(..))")
    public void transactionPointcut(){
    }

    @Before("transactionPointcut()")
    public void transactionAdvice(){
    }
    
}
