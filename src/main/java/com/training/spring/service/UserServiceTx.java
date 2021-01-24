package com.training.spring.service;

import com.training.spring.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceTx implements UserService{
    // [프록시의 기능 구분] 타깃 오브젝트
    UserService userService;

    public void setUserService(UserService userService){
        this.userService = userService;
    }

    // DB 커넥션 생성과 트랜잭션 경계설정 기능을 모두 사용할 수 있다
    private PlatformTransactionManager transactionManager;
    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }


    // [프록시의 기능 구분] 메소드의 구현과 위임
    @Override
    public void add(User user) {
        userService.add(user);
    }

    @Override
    public void upgradeLevels() {
        // [프록시의 기능 구분] 부가 기능 수행
        // 트랜잭션에 대한 조작이 필요할 때 전달해줄 데이터
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            userService.upgradeLevels();    // [프록시의 기능 구분] 위임

            // [프록시의 기능 구분] 부가 기능 수행
            this.transactionManager.commit(status);      // COMMIT

        } catch (Exception e){
            // [프록시의 기능 구분] 부가 기능 수행
            transactionManager.rollback(status);    // ROLLBACK
            throw e;
        }
    }
}
