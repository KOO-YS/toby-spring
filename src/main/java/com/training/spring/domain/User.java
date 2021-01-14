package com.training.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    String id;
    String name;
    String password;

    Level level;
    int login;      // 로그인 횟수
    int recommend;  // 추천 수

    String email;

//    Date lastUpgraded;

    public void upgradeLevel(){
        Level nextLevel = this.level.nextLevel();
//        this.lastUpgraded = new Date();
        if(nextLevel == null){
            throw new IllegalStateException(this.level+"은 업그레이드가 불가능합니다");
        } else {
            this.level = nextLevel;
        }
    }
}
