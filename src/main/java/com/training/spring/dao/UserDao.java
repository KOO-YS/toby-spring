package com.training.spring.dao;

import com.training.spring.domain.User;

import java.sql.*;

// 클래스 분리로 인한 상속 제거
public class UserDao {

    private ConnectionMaker connectionMaker;

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 기존 생성자 대신 set 메서드 추가
    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
//        Connection c = dataSource.getConnection();    FIXME : DataSource로 변환 필요
        Connection c = connectionMaker.makeNewConnection();

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
        Connection c = connectionMaker.makeNewConnection();
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
    
}
