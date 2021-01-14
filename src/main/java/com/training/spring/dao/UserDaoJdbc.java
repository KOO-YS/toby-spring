package com.training.spring.dao;

import com.mysql.cj.exceptions.MysqlErrorNumbers;
import com.training.spring.domain.Level;
import com.training.spring.domain.User;
import com.training.spring.exception.DuplicateUserIdException;
import com.training.spring.strategy.StatementStrategy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

// 클래스 분리로 인한 상속 제거
public class UserDaoJdbc implements UserDao{

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
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));
            user.setEmail("yaans@yaans.com");
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

    @Override
    public void add(User user) throws DuplicateUserIdException{     // 애플리케이션 레벨의 체크 예외
        // JdbcTemplate 이용
        try {
            this.jdbcTemplate.update("INSERT INTO users(id, name, password, level, login, recommend, email) VALUES(?,?,?,?,?,?,?)", user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
        } catch (DuplicateKeyException e){  // DataAccessException의 서브 클래스 DuplicateKeyExceptio으로 매핑되어 던져진다
            throw new DuplicateUserIdException(e);  // 중첩 예외
        }
    }

    @Override
    public void deleteAll(){
        // Spring에서 지원해주는 Jdbc Template 사용
        this.jdbcTemplate.update("DELETE FROM users");
    }

    @Override
    public User get(String id){
        // Spring에서 지원해주는 Jdbc Template 사용
        return this.jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new Object[]{id}, this.userMapper);
    }

    @Override
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

    @Override
    public List<User> getAll() {
        return this.jdbcTemplate.query("SELECT * FROM users ORDER BY id", this.userMapper);
    }

    @Override
    public int update(User user) {
        return this.jdbcTemplate.update("UPDATE users SET name=?, password=?, level=?, login=?, recommend=?, email=?" +
                                        " where id=?", user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
    }
}
