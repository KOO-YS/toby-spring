package com.training.spring.domain;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest
public class UserTest {

    User user;

    @Before
    public void setUp(){
        user = new User();
    }

    @Test
    public void upgradeLevel(){
        Level[] levels = Level.values();    // Returns an array containing the constants of this enum type, in the order they're declared
        for(Level level : levels){
            if(level.nextLevel() == null) continue;

            user.setLevel(level);
            user.upgradeLevel();

            assertThat(user.getLevel(), is(level.nextLevel()));
        }
    }

    /**
     * 더 이상 업그레이드를 할 경우가 없는 경우
     */
    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevel(){
        Level[] levels = Level.values();
        for(Level level : levels){
            if(level.nextLevel() != null) continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }
}
