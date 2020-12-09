package com.training.spring.dao;

import com.training.spring.domain.User;

import java.sql.SQLException;

// 런타임 오브젝트 관계 구조를 만들어줄 책임을 클라이언트에게 떠넘긴다
public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ConnectionMaker c = new DConnectionMaker();
        UserDao dao = new UserDao(c);

        User user = new User();
        user.setId("testId7");
        user.setName("테스트이름");
        user.setPassword("1234");

        dao.add(user);

        System.out.println(user.getId()+" 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId()+" 조회 성공");

    }
}
