package com.training.spring.dao;

import com.training.spring.domain.User;

import java.util.List;

public interface UserDao {
    public void add(User user);
    public void deleteAll();
    public User get(String id);
    public int getCount();
    public List<User> getAll();
    public int update(User user);
}
