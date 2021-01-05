package com.training.spring.service;

import com.training.spring.dao.UserDao;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

}
