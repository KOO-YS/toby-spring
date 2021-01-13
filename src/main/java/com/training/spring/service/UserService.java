package com.training.spring.service;

import com.training.spring.dao.UserDao;
import com.training.spring.domain.Level;
import com.training.spring.domain.User;
import org.springframework.jdbc.datasource.DataSourceUtils;
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
        // 트랜잭션 동기화 기능을 지원하는 스프링의 유틸리티 메소드
        TransactionSynchronizationManager.initSynchronization();        // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화

        Connection c = DataSourceUtils.getConnection(dataSource);       // DB 커넥션 생성과 동기화를 함께 해주는 유틸리티 메소드
        c.setAutoCommit(false);         // DB 커넥션을 생성하고 트랜잭션을 시작
        
        // 이후 DAO 작업은 모두 여기서부터 시작한 트랜잭션 안에서 진행된다

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user))       // 한 명씩 업그레이드 가능한지 확인
                    upgradeLevel(user);         // 한 명 업그레이드
            }
            c.commit();         // 정상 종료
        } catch (Exception e){
            c.rollback();       // 예외 발생 -> 데이터 되돌리기
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(c, dataSource);   // DB 커넥션을 안전하게 닫는다
            // 동기화 작업 종료 및 정리
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
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
