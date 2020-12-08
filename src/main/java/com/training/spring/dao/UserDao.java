package com.training.spring.dao;

import com.training.spring.domain.User;

import java.sql.*;

public abstract class UserDao {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        UserDao dao = new UserDao();
//
//        User user = new User();
//        user.setId("testId5");
//        user.setName("테스트이름");
//        user.setPassword("1234");
//
//        dao.add(user);
//
//        System.out.println(user.getId()+" 등록 성공");
//
//        User user2 = dao.get(user.getId());
//        System.out.println(user2.getName());
//        System.out.println(user2.getPassword());
//
//        System.out.println(user2.getId()+" 조회 성공");

    }

    /**
     * 중복 코드의 메소드 추출
     * -> 메소드 추출 기법(extract method)
     * -> 추상 메소드로 변경 (메소드 구현은 서브클래스가 담당한다)
     */
    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = getConnection();
        // 2. 사용자 등록을 위해 DB에 보낼 SQL 문장을 담을 Statement 생성 후 실행
        PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) values(?, ?, ?)");

        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        // 3. 리소스 오브젝트 닫기
        ps.close();
        c.close();

    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = getConnection();     // 중복 코드 메서드 추출 @getConnection
        PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }
    
    // =============================================================================================================
    //                                      DB 커넥션 방식에 따른 상속 클래스
    // =============================================================================================================

    public class NUserDao extends UserDao{
        @Override
        public Connection getConnection() throws ClassNotFoundException, SQLException {
            // 1. DB와 연결을 위한 Connection을 가져옴
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost/toby?characterEncoding=UTF-8&serverTimezone=UTC", "root", "root");

            return c;
        }
    }
    public class DUserDao extends UserDao{
        @Override
        public Connection getConnection() throws ClassNotFoundException, SQLException {
            // 1. DB와 연결을 위한 Connection을 가져옴
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost/toby?characterEncoding=UTF-8&serverTimezone=UTC", "root", "root");

            return c;
        }
    }
}
