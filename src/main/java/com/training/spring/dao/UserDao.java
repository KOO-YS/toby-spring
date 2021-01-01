package com.training.spring.dao;

import com.training.spring.domain.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    void deleteAll();
    User get(String id);
    int getCount();
    List<User> getAll();
}
