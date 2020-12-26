package com.training.spring.dao;

import com.training.spring.domain.User;
import com.training.spring.strategy.AddStatement;
import com.training.spring.strategy.DeleteAllStatement;
import com.training.spring.strategy.StatementStrategy;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.*;

// 클래스 분리로 인한 상속 제거
public class UserDao {

    // JdbcContext를 DI 받도록 만든다
    private JdbcContext jdbcContext;
    public void setJdbcContext(JdbcContext jdbcContext){
        this.jdbcContext = jdbcContext;
    }
    private ConnectionMaker connectionMaker;

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        this.jdbcContext.workWithStatementStrategy(
            new StatementStrategy() {
                @Override
                public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                    PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES(?,?,?)");

                    ps.setString(1, user.getId());          // user 정보를 따로 선언하지 않아도 된다
                    ps.setString(2, user.getName());
                    ps.setString(3, user.getPassword());
                    return ps;
                }
            }
        );
    }

    public void deleteAll() throws SQLException, ClassNotFoundException {
        this.jdbcContext.workWithStatementStrategy(
                 new StatementStrategy() {
                @Override
                public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                    PreparedStatement ps = c.prepareStatement("DELETE FROM users");
                    return ps;
                }
            }
        );
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        try {
            c = connectionMaker.makeNewConnection();
            ps = c.prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setString(1, id);

            rs = ps.executeQuery();
            if(rs.next()) {
                user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }

            // 일치하는 데이터가 없다면 예외 발생
            if(user == null) throw new EmptyResultDataAccessException(1);

        } catch (SQLException e){
            throw e;

        } finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e){
                    throw e;
                }
            }
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

        return user;
    }

    public int getCount() throws SQLException, ClassNotFoundException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            c = connectionMaker.makeNewConnection();
            ps = c.prepareStatement("SELECT COUNT(*) FROM users");

            rs = ps.executeQuery();
            rs.next();
            count = rs.getInt(1);

        } catch (SQLException e){
            throw e;

        } finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e){
                    throw e;
                }
            }
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
        return count;
    }

    /*
        변하지 않는 부분. context
        @Param 클라이언트가 컨텍스트를 호출할 때 넘겨줄 전략 파라미터
     */
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException, ClassNotFoundException {

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
