package com.training.spring.dao;

import javax.sql.CommonDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * DB 커넥션을 가져오는 오브젝트의 기능을 추상화해 사용할 수 있도록 만들어진 인터페이스
 */
@Deprecated
public interface DataSourceTemp extends CommonDataSource, Wrapper {
    Connection getConnection() throws SQLException;


}
