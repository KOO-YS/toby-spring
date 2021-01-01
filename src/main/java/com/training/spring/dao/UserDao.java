package com.training.spring.dao;

import com.mysql.cj.exceptions.MysqlErrorNumbers;
import com.training.spring.domain.User;
import com.training.spring.exception.DuplicateUserIdException;
import com.training.spring.strategy.StatementStrategy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

// 클래스 분리로 인한 상속 제거
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    // JdbcContext를 DI 받도록 만든다
    private JdbcContext jdbcContext;

    private ConnectionMaker connectionMaker;

    // 중복 추출
    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        }
    };

    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setJdbcContext(JdbcContext jdbcContext){
        this.jdbcContext = jdbcContext;
    }

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add(User user) throws DuplicateUserIdException, ClassNotFoundException {
        // SQLException 예외 처리 실습을 위해 잠시 SQLException를 던지는 메소드로 변경
//        this.jdbcTemplate.update("INSERT INTO users(id, name, password) VALUES(?,?,?)", user.getId(), user.getName(), user.getPassword());

        try {
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
        } catch (SQLException e){
            // 아이디 중복 예외 : 캐치해서 처리할 수 있는 문제이므로 (from 체크 예외에서 -> )런타임 에러로 만들어서 사용
            if(e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY)
                throw new DuplicateUserIdException(e);      // 예외 전환
            else
                throw new RuntimeException();

        }
    }

    public void deleteAll(){
        // Spring에서 지원해주는 Jdbc Template 사용
        this.jdbcTemplate.update("DELETE FROM users");
    }


    public User get(String id){
        // Spring에서 지원해주는 Jdbc Template 사용
        return this.jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new Object[]{id}, this.userMapper);
    }

    public int getCount(){
        // Spring에서 지원해주는 Jdbc Template 사용 1
//        return this.jdbcTemplate.query(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                return con.prepareStatement("SELECT COUNT(*) FROM users");
//            }
//        }, new ResultSetExtractor<Integer>() {
//            @Override
//            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//                rs.next();
//                return rs.getInt(1);
//            }
//        });
        // Spring에서 지원해주는 Jdbc Template 사용 2
        return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.TYPE);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("SELECT * FROM users ORDER BY id", this.userMapper);
    }

}
