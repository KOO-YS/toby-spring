package com.training.spring.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CountingConnectionMaker를 적용한 후 런타임 오브젝트 의존관계
 */
@Configuration
public class CountingDaoFactory {
    /**
     * 커넥션을 요청할 오브젝트
     */
    @Bean
    public UserDao userDao(){
//        return new UserDao(connectionMaker());
        UserDao userDao = new UserDao();
        userDao.setConnectionMaker(connectionMaker());
        return userDao;
    }

    /**
     * DB 커넥션을 카운팅할 오브젝트
     */
    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    /**
     * 진짜 DB 커넥션을 이용할 오브젝트
     */
    @Bean
    public ConnectionMaker realConnectionMaker(){
        return new DConnectionMaker();
    }
}
