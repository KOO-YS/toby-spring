package com.training.spring.dao;

import com.training.spring.domain.User;
import com.training.spring.exception.DuplicateUserIdException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserDaoTest {
    public static void main(String[] args) {
        JUnitCore.main("com.training.spring.dao.UserDaoTest");
    }

    // 픽스처 : 테스트를 수행하는 데 필요한 오브젝트
    private UserDao dao;    // 테스트 메소드에서 접근할 수 있도록 인스턴스 변수로 변경
    @Autowired
    private DataSource dataSource;
    @Before     // @Test 메소드가 실행되기 전 먼저 실행해야하는 메소드 정의
    public void setUp(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        // ** 구현 기술이 달라진다면 구현 클래스를 여기서 변경해줌!
        this.dao = context.getBean("userDaoJdbc", UserDaoJdbc.class);
        this.dataSource = context.getBean("dataSource", DataSource.class);
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

    @Test
    public void getAll() throws SQLException, ClassNotFoundException {
        dao.deleteAll();

        List<User> check = dao.getAll();
        assertThat(check.size(), is(0));
        
        for(int i=1; i<=3; i++){
            // User 생성
            User now = new User("userId"+i, "userName"+i, "1234");
            dao.add(now);

            // DB User 리스트 받아오기
            List<User> users = dao.getAll();
            // 리스트 크기 비교
            assertThat(users.size(), is(i));

            // 동등성 비교
            checkSameUser(users.get(i-1), now);
        }

    }

    // @Test를 붙이지 않는다 -> getAll()에서 반복되기 때문
    private void checkSameUser(User user, User now) {
        assertThat(user.getId(), is(now.getId()));
        assertThat(user.getName(), is(now.getName()));
        assertThat(user.getPassword(), is(now.getPassword()));
    }

//    @Test(expected = DataAccessException.class)
    @Test(expected = DuplicateUserIdException.class)
    public void duplicateKey(){
        dao.deleteAll();

        User user = new User();
        user.setId("JUnitId");
        user.setName("testName");
        user.setPassword("1234");

        dao.add(user);
        dao.add(user);      // 중복 예외 발생
    }

    @Test
    public void sqlEceptionTranslate(){
        dao.deleteAll();
        User user = new User();
        user.setId("JUnitId");
        user.setName("testName");
        user.setPassword("1234");

        try {
            dao.add(user);
            dao.add(user);      // 중복 예외 발생
        } catch (DataAccessException e){
            SQLException sqlEx = (SQLException) e.getRootCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
//            assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
        }
    }

}
