package com.training.spring.dao;

import com.training.spring.domain.User;
import com.training.spring.factory.CountingDaoFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;
@SpringBootTest
public class UserDaoCountingTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        // @Configuration이 붙은 자바 코드를 설정정보로 사용하기 위한 생성자
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDaoJdbc dao = context.getBean("userDao", UserDaoJdbc.class);

        User user = new User();
        user.setId("1234");
        user.setName("테스트이름");
        user.setPassword("1234");

        dao.add(user);

        System.out.println(user.getId()+" 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId()+" 조회 성공");

        // getBean() : 의존관계 검색 -> 이름을 이용해 어떤 빈이든 가져올 수 있다 (스프링 컨테이너가 만든 오브젝트가 아닐지라도)
        CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
        System.out.println("커넥션 요청 횟수 : "+ccm.getCounter());

    }
}
