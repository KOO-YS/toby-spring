package com.training.spring.service;

import com.training.spring.dao.UserDao;
import com.training.spring.domain.Level;
import com.training.spring.domain.User;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    UserDao userDao;

    private DataSource dataSource;      // Connection 생성 시 사용할 DataSource DI

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

    public void upgradeLevels() throws SQLException {
        // JDBC 트랜잭션 추상 오브젝트 생성 (생성과 함께 트랜잭션 시작)
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);       // JDBC 로컬 트랜잭션을 이용
        // PlatformTransactionManager 로 시작한 트랜잭션 ::: 트랜잭션 동기화 저장소에 저장
        
        // 트랜잭션에 대한 조작이 필요할 때 전달해줄 데이터
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user))       // 한 명씩 업그레이드 가능한지 확인
                    upgradeLevel(user);         // 한 명 업그레이드
            }

            transactionManager.commit(status);      // COMMIT

        } catch (Exception e){
            transactionManager.rollback(status);    // ROLLBACK
            throw e;
        }
    }

    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    public boolean canUpgradeLevel(User user){
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC:
                return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER:
                return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
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
