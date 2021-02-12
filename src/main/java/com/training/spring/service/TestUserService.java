package com.training.spring.service;

import com.training.spring.domain.User;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class TestUserService extends UserServiceImpl {
    private String id = "madnite1";

    @Override
    @Transactional(readOnly = true)     // FIXME : 그대로 update() 작동
    public List<User> getAll() {
        for(User user: super.getAll()){
            super.update(user);     // reaoOnly 속성에 강제로 쓰기 시도
        }
        return null;    // 별 의미 없음
    }

    public TestUserService(){}

    public TestUserService(String id){
        this.id = id;       // 예외를 발생시킬 User 오브젝트 지정
    }

    public void upgradeLevel(User user){
        if(user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }

    static class TestUserServiceException extends RuntimeException {}
}

