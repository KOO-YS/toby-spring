package com.training.spring.dao;

import com.training.spring.strategy.StatementStrategy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {

    private ConnectionMaker connectionMaker;
    private DataSource dataSource;

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException, ClassNotFoundException {

        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = dataSource.getConnection();
//            c = connectionMaker.makeNewConnection();
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
    public void executeSql(final String query) throws SQLException, ClassNotFoundException {
        workWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.prepareStatement(query);
            }
        });
    }
}
