package com.training.spring.dao;

import com.training.spring.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserDaoTest {
    public static void main(String[] args) {
        JUnitCore.main("com.training.spring.dao.UserDaoTest");
    }

    // 픽스처 : 테스트를 수행하는 데 필요한 오브젝트
    private UserDao dao;    // 테스트 메소드에서 접근할 수 있도록 인스턴스 변수로 변경

    @Before     // @Test 메소드가 실행되기 전 먼저 실행해야하는 메소드 정의
    public void setUp(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        this.dao = context.getBean("userDao", UserDao.class);
    }

    @Test       // JUnit 테스트용 메소드는 반드시 public 선언 & 반환 void
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));      // 데이터가 모두 삭제되었는지 레코드 수 확인

        User user = new User();
        user.setId("JUnitId");
        user.setName("testName");
        user.setPassword("1234");

        dao.add(user);
        assertThat(dao.getCount(), is(1));      // User 데이터를 추가함에 따라 레코드 수가 변했는지 확인

        User user2 = dao.get(user.getId());

        // 값이 일치하는지 테스트
        assertThat(user2.getName(), is(user.getName()));        // is() : Matcher의 일종 equals()로 비교해주는 기능
        assertThat(user2.getPassword(), is(user.getPassword()));

    }
    @Test
    public void count() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        // Count가 제대로 동작하는지 테스트
        for(int i=1; i<=3; i++){
            dao.add(new User("userId"+i, "userName"+i, "1234"));
            assertThat(dao.getCount(), is(i));
        }
    }

    // 예외상황에 대비한 테스트
    @Test(expected = EmptyResultDataAccessException.class)      // 테스트 도중 발생할 것으로 예상되는 예외 클래스 지정
    public void getUserFailure() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.get("unknown");

    }
}
