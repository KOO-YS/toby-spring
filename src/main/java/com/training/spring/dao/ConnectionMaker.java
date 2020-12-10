package com.training.spring.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface ConnectionMaker {
    // DB 커넥션을 가져오는 메소드
    public Connection makeNewConnection()throws ClassNotFoundException, SQLException;

    
}
//    public Connection makeNewConnection()throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/toby?characterEncoding=UTF-8&serverTimezone=UTC", "root", "root");
//
//        return c;
//    }