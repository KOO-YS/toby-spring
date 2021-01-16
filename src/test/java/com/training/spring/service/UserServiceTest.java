package com.training.spring.service;

import com.training.spring.dao.UserDao;
import com.training.spring.dao.UserDaoJdbc;
import com.training.spring.domain.Level;
import com.training.spring.domain.User;
import com.training.spring.factory.BeanFactory;
import com.training.spring.util.DummyMailSender;
import com.training.spring.util.MockMailSender;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.training.spring.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.training.spring.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userServiceTx;      // 타입이 일치하는 빈이 2개 이상일 때, 필드명을 우선적인 기준으로 빈을 찾는다
    @Autowired
    UserDao userDao;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;

    List<User> userList;

    @Before
    public void setUpBean(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanFactory.class);
        this.userServiceTx = context.getBean("userServiceTx", UserServiceTx.class);
        this.userServiceImpl = context.getBean("userServiceImpl", UserServiceImpl.class);
        this.userDao = context.getBean("userDaoJdbc", UserDaoJdbc.class);
//        this.dataSource = context.getBean("dataSource", DataSource.class);
        this.transactionManager = context.getBean("transactionManager", DataSourceTransactionManager.class);
        this.mailSender = context.getBean("dummyMailSender", DummyMailSender.class);
    }

    @Before
    public void setFixture(){
        userList = Arrays.asList(
                                                                            // 상수를 사용해 어떤 의도로 값을 넣었는지 이해가 쉬워짐
          new User("Apple", "사과", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "yaans@yaans.com"),
          new User("Banana","바나나", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "yaans@yaans.com"),
          new User("Cherry","체리", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1,"yaans@yaans.com"),
          new User("Date","대추", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD,"yaans@yaans.com"),
          new User("Egg","계란", "p5",Level.GOLD, 100, Integer.MAX_VALUE,"yaans@yaans.com")
        );
    }

    @Test
    @DirtiesContext     // 컨텍스트의 DI 설정을 변경하는 테스트
    public void upgradeLevels() throws SQLException {
        userDao.deleteAll();

        for(User u : userList) userDao.add(u);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        checkLevelUpgraded(userList.get(0), false);
        checkLevelUpgraded(userList.get(1), true);
        checkLevelUpgraded(userList.get(2), false);
        checkLevelUpgraded(userList.get(3), true);
        checkLevelUpgraded(userList.get(4), false);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(userList.get(1).getEmail()));
        assertThat(request.get(1), is(userList.get(3).getEmail()));

    }

    /**
     * @param upgraded 업그레이드가 된지/안된지
     */
    public void checkLevelUpgraded(User user, boolean upgraded){
        User userUpdate = userDao.get(user.getId());
        if(upgraded){
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));     // 업그레이드
        } else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));                 // 그대로
        }
    }

    public void checkLevel(User user, Level expectedLevel){
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

    @Test
    public void add(){
        userDao.deleteAll();

        User userWithLevel = userList.get(4);
        User userWithoutLevel = userList.get(0);
        userWithoutLevel.setLevel(null);    // 레벨이 비어있는 사용자로 설정 -> BASIC으로 자동 설정 요구

        userServiceTx.add(userWithLevel);
        userServiceTx.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

    @Test
    public void upgradeAllOrNothing(){
        UserServiceImpl testUserService = new TestUserService(userList.get(3).getId());
        testUserService.setUserDao(this.userDao);       // 수동 DI
        testUserService.setTransactionManager(transactionManager);
        testUserService.setMailSender(mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService);

        userDao.deleteAll();
        for(User user : userList){
            userDao.add(user);
        }

        try {
            // 트랜잭션 기능을 분리한 오브젝트를 통해 예외 발생용 TestUserService가 호출되게 해야 한다
            txUserService.upgradeLevels();                // 이 메소드가 정상 종료 되면 안된다!
            fail("TestUserServiceException expected ");
        } catch (TestUserService.TestUserServiceException e){

        }

        checkLevelUpgraded(userList.get(1), false);
    }

}
