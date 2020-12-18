package com.training.spring.dao;

import com.training.spring.domain.User;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        // @Configuration이 붙은 자바 코드를 설정정보로 사용하기 위한 생성자 
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("confirmId");
        user.setName("testName");
        user.setPassword("1234");

        dao.add(user);

        System.out.println(user.getId()+" 등록 성공");

        User user2 = dao.get(user.getId());

        // 값이 일치하는지 테스트
        if(!user.getName().equals(user2.getName())){
            System.out.println("test fail : Name");
        } else if(!user.getPassword().equals(user2.getPassword())){
            System.out.println("test fail : Password");
        } else {
            System.out.println("test success");
            System.out.println(user2.toString());
        }

    }
}
