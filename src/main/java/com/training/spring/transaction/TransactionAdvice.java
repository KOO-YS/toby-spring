package com.training.spring.transaction;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {
    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * @param invocation 타깃을 호출하는 기능을 가진 콜백 오브젝트를 프록시로부터 받는다(어드바이스는 특정 타깃에 의존하지 않고 재사용 가능)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object result = invocation.proceed();
            this.transactionManager.commit(status);
            return result;
        } catch (RuntimeException e){
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
