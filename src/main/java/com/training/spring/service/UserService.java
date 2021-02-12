package com.training.spring.service;

import com.training.spring.domain.User;

import java.util.List;

public interface UserService {
    void add(User user);
    void upgradeLevels();

    User get(String id);
    List<User> getAll();
    void deleteAll();
    void update(User user);

}
