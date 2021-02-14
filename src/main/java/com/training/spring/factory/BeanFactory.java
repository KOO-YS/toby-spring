package com.training.spring.factory;

import com.training.spring.dao.ConnectionMaker;
import com.training.spring.dao.DConnectionMaker;
import com.training.spring.dao.UserDaoJdbc;
import com.training.spring.service.TestUserService;
import com.training.spring.service.UserServiceImpl;
import com.training.spring.service.UserServiceTx;
import com.training.spring.sqlservice.SimpleSqlService;
import com.training.spring.transaction.TransactionAdvice;
import com.training.spring.util.DummyMailSender;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration          // -> 오브젝트 설정을 담당하는 클래스 인식
public class BeanFactory {

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
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }

    @Bean               // -> 오브젝트 생성 메소드를 위함
    public UserDaoJdbc userDaoJdbc(){

        UserDaoJdbc userDao = new UserDaoJdbc();
        userDao.setSqlService(sqlService());
        userDao.setConnectionMaker(connectionMaker());
        userDao.setDataSource(dataSource());        // DataSource 추가
        return userDao;
    }

    @Bean
    public UserServiceImpl userServiceImpl(){
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDaoJdbc());
        userService.setTransactionManager(transactionManager());
        userService.setMailSender(dummyMailSender());
        return userService;
    }

    // 인터페이스를 통한 데코레이터 정의
    @Bean
    public UserServiceTx userServiceTx(){
        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setTransactionManager(transactionManager());
        userServiceTx.setUserService(userServiceImpl());        // 타깃 오브젝트 -> 런타임 시의 다이내믹한 구성 방법 
        return userServiceTx;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public MailSender mailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("mail.server.com");
        return mailSender;
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

    @Bean
    public TransactionAdvice transactionAdvice(){
        TransactionAdvice advice = new TransactionAdvice();
        advice.setTransactionManager(transactionManager());
        return advice;
    }

    @Bean
    public NameMatchMethodPointcut transaactionPointcut(){
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor(){
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setAdvice(transactionAdvice());
        advisor.setPointcut(transaactionPointcut());
        return advisor;
    }

    @Bean
    public TestUserService testUserService(){
        TestUserService test = new TestUserService();
        test.setUserDao(userDaoJdbc());
        test.setTransactionManager(transactionManager());
        test.setMailSender(dummyMailSender());
        return test;
    }

    @Bean
    public AspectJExpressionPointcut transactionPointcut(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* *..*ServiceImpl.upgrade*(..))");
        return pointcut;
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
}
