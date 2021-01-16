package com.training.spring.service;

import com.training.spring.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceTx implements UserService{
    UserService userService;

    public void setUserService(UserService userService){
        this.userService = userService;
    }

    // DB 커넥션 생성과 트랜잭션 경계설정 기능을 모두 사용할 수 있다
    private PlatformTransactionManager transactionManager;
    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }


    @Override
    public void add(User user) {
        userService.add(user);
    }

    @Override
    public void upgradeLevels() {
        // 트랜잭션에 대한 조작이 필요할 때 전달해줄 데이터
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            userService.upgradeLevels();

            this.transactionManager.commit(status);      // COMMIT

        } catch (Exception e){
            transactionManager.rollback(status);    // ROLLBACK
            throw e;
        }
    }
}
