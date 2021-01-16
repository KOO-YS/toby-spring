package com.training.spring.service;

import com.training.spring.dao.UserDao;
import com.training.spring.domain.Level;
import com.training.spring.domain.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService{
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

    /**
     * 순수하게 사용자 레벨 업그레이드를 담당하는 비즈니스 로직 코드만 독립적인 메소드에 담기
     */
    public void upgradeLevels(){

        List<User> users = userDao.getAll();
        for (User user : users) {
            if (canUpgradeLevel(user))       // 한 명씩 업그레이드 가능한지 확인
                upgradeLevel(user);         // 한 명 업그레이드
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
