package com.training.spring.dao;

import com.training.spring.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        // 기존 코드
//        ConnectionMaker c = new DConnectionMaker();
//        UserDao dao = new UserDao(c);

        // Factory 생성 후 -> UserDaoTest.java는 UserDao 테스트에만 충실
        UserDao dao = new DaoFactory().userDao();

        User user = new User();
        user.setId("te2Id7");
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
