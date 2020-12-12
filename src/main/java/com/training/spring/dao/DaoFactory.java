package com.training.spring.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration          // -> 오브젝트 설정을 담당하는 클래스 인식
public class DaoFactory {

    @Bean
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }

    @Bean               // -> 오브젝트 생성 메소드를 위함
    public UserDao userDao(){
        return new UserDao(connectionMaker());      // 이 메소드 안에서 connection 정보를 바꿀 일이 없어졌다
    }
}
