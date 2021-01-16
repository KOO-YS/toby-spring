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
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.training.spring.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.training.spring.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

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
    public void mockUpgradeLevels(){
        UserServiceImpl serviceImpl = new UserServiceImpl();

        // 다이나믹한 목 오브젝트 생성과 메소드의 리턴 값 설정, DI
        UserDao mockUserDao = mock(UserDao.class);
        // mockUserDao.getAll()이 호출됐을 때, userList를 리턴해주어라
        when(mockUserDao.getAll()).thenReturn(this.userList);
        serviceImpl.setUserDao(mockUserDao);

        // 리턴값이 없는 메소드를 가진 목 오브젝트 생성
        MailSender mockMailSender = mock(MailSender.class);
        serviceImpl.setMailSender(mockMailSender);

        serviceImpl.upgradeLevels();

        // User 타입의 오브젝트를 파라미터로 받으며 update() 메소드가 2번 호출됐는지 확인
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(userList.get(1));
        assertThat(userList.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(userList.get(3));
        assertThat(userList.get(3).getLevel(), is(Level.GOLD));
        // -> 목 오브젝트가 제공하는 검증 기능을 통해서 어떤 메소드가 몇 번 호출됐는지, 파라미터는 무엇인지 확인할 수 있다

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        // 파라미터를 정밀하게 검사하기 위해 캡쳐 가능
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(userList.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(userList.get(3).getEmail()));
    }

    @Test
    public void upgradeLevels() throws SQLException {
        // 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성
        // 완전히 고립된 테스트만을 위해 독립적으로 동작하는 테스트 대상을 사용할 것이기 떄문에 스프링 컨테이너에서 빈을 가져올 필요가 없다
        UserServiceImpl serviceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.userList);
        serviceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        serviceImpl.setMailSender(mockMailSender);

        serviceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();

        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "Banana", Level.SILVER);
        checkUserAndLevel(updated.get(1), "Date", Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(userList.get(1).getEmail()));
        assertThat(request.get(1), is(userList.get(3).getEmail()));
    }
    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
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

    static class MockUserDao implements UserDao{
        private List<User> userList;
        private List<User> updated = new ArrayList<>();

        private MockUserDao(List<User> userList){
            this.userList = userList;
        }

        public List<User> getUpdated() {
            return updated;
        }

        // Stub 기능 제공
        @Override
        public List<User> getAll(){
            return this.userList;
        }

        // 목 오브젝트 기능 제공
        @Override
        public void update(User user) {
            updated.add(user);
        }

        /**
         *  테스트로 사용되지 않는 메소드들
         */

        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }

    }
}
