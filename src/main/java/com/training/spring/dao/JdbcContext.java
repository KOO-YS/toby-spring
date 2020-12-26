package com.training.spring.dao;

import com.training.spring.strategy.StatementStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {

    private ConnectionMaker connectionMaker;

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException, ClassNotFoundException {

        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = connectionMaker.makeNewConnection();
            ps = stmt.makePreparedStatement(c);
            ps.executeUpdate();

        } catch (SQLException e){
            throw e;

        } finally {
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e){
                    throw e;
                }
            }
            if(c != null){
                try {
                    c.close();
                } catch (SQLException e){
                    throw e;
                }
            }
        }
    }
}
