package com.training.spring.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransactionHandler implements InvocationHandler {
    private Object target;          // 부가 기능을 제공할 타깃 오브젝트
    private PlatformTransactionManager transactionManager;      // 트랜잭션 기능을 제공하기 위해 필요한 트랜잭션 매니저
    private String pattern;         // 트랜잭션을 적용할 메소드 이름 패턴

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().startsWith(pattern))        // 트랜잭션 적용 대상 메소드를 선별해서 트랜잭션 경계 설정 기능 부여
            return invokeInTransaction(method, args);
        return method.invoke(target, args);
    }

    private Object invokeInTransaction(Method method, Object[] args) throws Throwable{
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object result = method.invoke(target, args);
            this.transactionManager.commit(status);
            return result;

        } catch (IllegalAccessException e1) {
            this.transactionManager.rollback(status);
            throw e1.getCause();

        } catch (InvocationTargetException e2) {        //리플렉션 메소드 발생 예외는 InvocationTargetException로 포장되어 전달된다
            this.transactionManager.rollback(status);
            throw e2.getTargetException();              // 중첩되어 있는 예외를 확인해보자
        }
    }
}
