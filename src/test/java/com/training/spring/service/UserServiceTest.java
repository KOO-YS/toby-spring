package com.training.spring.service;

import com.training.spring.dao.UserDao;
import com.training.spring.dao.UserDaoJdbc;
import com.training.spring.domain.Level;
import com.training.spring.domain.User;
import com.training.spring.factory.DaoFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static com.training.spring.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.training.spring.service.UserService.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;

    List<User> userList;

    @Before
    public void setUpBean(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        this.userService = context.getBean("userService", UserService.class);
        this.userDao = context.getBean("userDaoJdbc", UserDaoJdbc.class);

    }

    @Before
    public void setFixture(){
        userList = Arrays.asList(
                                                                            // 상수를 사용해 어떤 의도로 값을 넣었는지 이해가 쉬워짐
          new User("Apple", "사과", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
          new User("Banana","바나나", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
          new User("Cherry","체리", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1),
          new User("Date","대추", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
          new User("Egg","계란", "p5",Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels(){
        userDao.deleteAll();

        for(User u : userList){
            userDao.add(u);
        }

        userService.upgradeLevels();

        checkLevelUpgraded(userList.get(0), false);
        checkLevelUpgraded(userList.get(1), true);
        checkLevelUpgraded(userList.get(2), false);
        checkLevelUpgraded(userList.get(3), true);
        checkLevelUpgraded(userList.get(4), false);

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

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }

    @Test
    public void upgrageAllOrNothing(){
        UserService testUserService = new TestUserService(userList.get(3).getId());
        testUserService.setUserDao(this.userDao);       // 수동 DI

        userDao.deleteAll();
        for(User user : userList){
            userDao.add(user);
        }

        try {
            testUserService.upgradeLevels();                // 이 메소드가 정상 종료 되면 안된다!
            fail("TestUserServiceException expected ");
        } catch (TestUserService.TestUserServiceException e){

        }

        checkLevelUpgraded(userList.get(1), false);
    }

}
