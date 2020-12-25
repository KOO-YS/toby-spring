package com.training.spring.dao;

import com.training.spring.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

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
        Connection c = null;
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement("INSERT INTO users(id, name, password) values(?, ?, ?)");

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();

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

    public void deleteAll() throws SQLException, ClassNotFoundException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = connectionMaker.makeNewConnection();
            ps = c.prepareStatement("DELETE FROM users");

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

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        try {
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
}
