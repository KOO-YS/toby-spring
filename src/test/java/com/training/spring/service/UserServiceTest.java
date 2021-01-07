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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

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
          new User("Apple", "사과", "p1", Level.BASIC, 49, 0),
          new User("Banana","바나나", "p2", Level.BASIC, 50, 0),
          new User("Cherry","체리", "p3", Level.SILVER, 60, 29),
          new User("Date","대추", "p4", Level.SILVER, 60, 30),
          new User("Egg","계란", "p5",Level.GOLD, 100, 100)
        );
    }

    @Test
    public void upgradeLevels(){
        userDao.deleteAll();

        for(User u : userList){
            userDao.add(u);
        }

        userService.upgradeLevels();

        checkLevel(userList.get(0), Level.BASIC);
        checkLevel(userList.get(1), Level.SILVER);
        checkLevel(userList.get(2), Level.SILVER);
        checkLevel(userList.get(3), Level.GOLD);
        checkLevel(userList.get(4), Level.GOLD);

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
}
