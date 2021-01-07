package com.training.spring.service;

import com.training.spring.dao.UserDao;
import com.training.spring.domain.Level;
import com.training.spring.domain.User;

import java.util.List;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

    public void upgradeLevels(){
        List<User> users = userDao.getAll();
        for(User user : users){
            Boolean changed = null;     // level 변경 플래그

            // BASIC 레벨 업그레이드
            if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){
                user.setLevel(Level.SILVER);
                changed = true;
            }
            // SILVER 레벨 업그레이드
            else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){
                user.setLevel(Level.GOLD);
                changed = true;
            }
            else if(user.getLevel() == Level.GOLD){
                changed = false;
            }
            else {
                changed = false;
            }
            if(changed){
                userDao.update(user);
            }

        }
    }

    public boolean canUpgradeLevel(User user){
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC:
                return (user.getLogin() >= 50);
            case SILVER:
                return (user.getRecommend() >= 30);
            case GOLD:
                return false;
            default:
                throw new IllegalArgumentException("unknown Level : "+currentLevel);
        }

    }
    public void add(User user){
        if(user.getLevel() == null){
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
