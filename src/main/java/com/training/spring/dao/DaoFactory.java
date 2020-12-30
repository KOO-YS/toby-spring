package com.training.spring.dao;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration          // -> 오브젝트 설정을 담당하는 클래스 인식
public class DaoFactory {

    @Bean
    public DataSource dataSource(){
//        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        // DB 연결정보를 수정자 메소드를 통해 넣어준다 [오브젝트 레벨에서 DB 연결 방식]
//        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);     // Cannot resolve symbol 'Driver' -> File -> invalidate Caches/restart
//        dataSource.setUrl("jdbc:mysql://localhost/toby?characterEncoding=UTF-8&serverTimezone=UTC");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");

//        return dataSource;

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://localhost/toby?characterEncoding=UTF-8&serverTimezone=UTC");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("root");
        return dataSourceBuilder.build();
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }

    @Bean               // -> 오브젝트 생성 메소드를 위함
    public UserDao userDao(){

        // delete & add 를 위한 
        JdbcContext jdbcContext = new JdbcContext();
        jdbcContext.setConnectionMaker(connectionMaker());
        jdbcContext.setDataSource(dataSource());        // DataSource 추가
        
        // 기존 메소드들을 위해 남겨둠
        UserDao userDao = new UserDao();
        userDao.setConnectionMaker(connectionMaker());
        userDao.setJdbcContext(jdbcContext);        // userDao에 jdbcContext 연결
        userDao.setDataSource(dataSource());        // DataSource 추가
        return userDao;
//        return new UserDao(connectionMaker());      // 이 메소드 안에서 connection 정보를 바꿀 일이 없어졌다
    }
}
