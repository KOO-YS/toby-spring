package com.training.spring.factory;

import com.training.spring.dao.ConnectionMaker;
import com.training.spring.dao.CountingConnectionMaker;
import com.training.spring.dao.DConnectionMaker;
import com.training.spring.dao.UserDaoJdbc;
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
    public UserDaoJdbc userDao(){
//        return new UserDao(connectionMaker());
        UserDaoJdbc userDao = new UserDaoJdbc();
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
