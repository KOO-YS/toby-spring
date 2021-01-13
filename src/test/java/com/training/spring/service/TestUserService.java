package com.training.spring.service;

import com.training.spring.domain.User;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestUserService extends UserService{
    private String id;

    TestUserService(String id){
        this.id = id;       // 예외를 발생시킬 User 오브젝트 지정
    }

    public void upgradeLevel(User user){
        if(user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }

    static class TestUserServiceException extends RuntimeException {}
}

