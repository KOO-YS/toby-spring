package com.training.spring;

import com.training.spring.dao.UserDaoJdbc;
import com.training.spring.service.TestUserService;
import com.training.spring.service.UserServiceImpl;
import com.training.spring.service.UserServiceTx;
import com.training.spring.sqlservice.SimpleSqlService;
import com.training.spring.sqlservice.SqlService;
import com.training.spring.util.DummyMailSender;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

/**
 * DI 메타정보로 사용될 클래스
 */
@Configuration
public class TestApplicationContext {

//    @Autowired
//    SqlService sqlService;

    @Bean
    public DataSource dataSource(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://localhost/toby?characterEncoding=UTF-8&serverTimezone=UTC");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("root");
        return dataSourceBuilder.build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());
        return tm;
    }
    @Bean
    public UserDaoJdbc userDaoJdbc(){
        UserDaoJdbc userDao = new UserDaoJdbc();
//        userDao.setSqlService(sqlService());
        userDao.setDataSource(dataSource());
        return userDao;
    }

    @Bean
    public UserServiceImpl userServiceImpl(){
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDaoJdbc());
        userService.setMailSender(mailSender());
        return userService;
    }

    @Bean
    public TestUserService testUserService(){
        TestUserService test = new TestUserService();
        test.setUserDao(userDaoJdbc());
        test.setMailSender(mailSender());
        return test;
    }
    @Bean
    public MailSender mailSender(){
        return new DummyMailSender();
    }

    @Bean
    public SimpleSqlService sqlService(){
        SimpleSqlService service = new SimpleSqlService();
        Map<String, String> map = new HashMap<>();
        map.put("userAdd", "INSERT INTO users(id, name, password, level, login, recommend, email) VALUES(?,?,?,?,?,?,?)");
        map.put("userGet","SELECT * FROM users WHERE id = ?");
        map.put("userGetAll","SELECT * FROM users ORDER BY id");
        map.put("userDeleteAll","DELETE FROM users");
        map.put("userGetCount","SELECT COUNT(*) FROM users");
        map.put("userUpdate","UPDATE users SET name=?, password=?, level=?, login=?, recommend=?, email=? where id=?");


        service.setSqlMap(map);

        return service;
    }

    @Bean
    public UserServiceTx userServiceTx(){
        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setTransactionManager(transactionManager());
        userServiceTx.setUserService(userServiceImpl());        // 타깃 오브젝트 -> 런타임 시의 다이내믹한 구성 방법
        return userServiceTx;
    }

    @Bean
    public MailSender dummyMailSender(){
        return new DummyMailSender();
    }

    @Bean
    public ProxyFactoryBean userService(){
        ProxyFactoryBean userService = new ProxyFactoryBean();
        userService.setTarget(userServiceImpl());
        userService.setInterceptorNames("transactionAdvisor");
        return userService;
    }
}
