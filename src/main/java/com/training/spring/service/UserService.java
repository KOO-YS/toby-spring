package com.training.spring.service;

import com.training.spring.dao.UserDao;
import com.training.spring.domain.Level;
import com.training.spring.domain.User;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class UserService {
    final String TEMP_EMAIL = "yaans@yaans.com";
    UserDao userDao;
    MailSender mailSender;

    public void setMailSender(MailSender mailSender){
        this.mailSender = mailSender;
    }

    // DB 커넥션 생성과 트랜잭션 경계설정 기능을 모두 사용할 수 있다
    private PlatformTransactionManager transactionManager;
    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

    public void upgradeLevels() throws SQLException {
        // 트랜잭션에 대한 조작이 필요할 때 전달해줄 데이터
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user))       // 한 명씩 업그레이드 가능한지 확인
                    upgradeLevel(user);         // 한 명 업그레이드
            }

            this.transactionManager.commit(status);      // COMMIT

        } catch (Exception e){
            transactionManager.rollback(status);    // ROLLBACK
            throw e;
        }
    }

    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user){
        // (스프링이 재공하는 )JavaMail을 사용해 메일 발송 기능을 제공하는 JavaMailSenderImpl 클래스
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("mail.server.com");

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom(TEMP_EMAIL);

        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText(user.getName()+" 사용자님의 등급이 "+user.getLevel().name()+"로 업그레이드 되었습니다");

        this.mailSender.send(mailMessage);
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
