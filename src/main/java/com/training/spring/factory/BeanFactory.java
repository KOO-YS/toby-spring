package com.training.spring.factory;

import com.training.spring.dao.ConnectionMaker;
import com.training.spring.dao.DConnectionMaker;
import com.training.spring.dao.UserDaoJdbc;
import com.training.spring.service.UserService;
import com.training.spring.service.UserServiceImpl;
import com.training.spring.service.UserServiceTx;
import com.training.spring.transaction.TxProxyFactoryBean;
import com.training.spring.util.DummyMailSender;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

@Configuration          // -> 오브젝트 설정을 담당하는 클래스 인식
public class BeanFactory {

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
    public UserDaoJdbc userDaoJdbc(){

        // delete & add 를 위한 
//        JdbcContext jdbcContext = new JdbcContext();
//        jdbcContext.setConnectionMaker(connectionMaker());
//        jdbcContext.setDataSource(dataSource());        // DataSource 추가
        
        // 기존 메소드들을 위해 남겨둠
        UserDaoJdbc userDao = new UserDaoJdbc();
        userDao.setConnectionMaker(connectionMaker());
//        userDao.setJdbcContext(jdbcContext);        // userDao에 jdbcContext 연결
        userDao.setDataSource(dataSource());        // DataSource 추가
        return userDao;
//        return new UserDao(connectionMaker());      // 이 메소드 안에서 connection 정보를 바꿀 일이 없어졌다
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
    public TxProxyFactoryBean userService(){
        TxProxyFactoryBean userService = new TxProxyFactoryBean();
        userService.setTarget(userServiceImpl());
        userService.setTransactionManager(transactionManager());
        userService.setPattern("upgradeLevels");
        userService.setServiceInterface(UserService.class);
        return userService;
    }
}
