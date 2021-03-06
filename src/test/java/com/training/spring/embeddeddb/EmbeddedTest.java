package com.training.spring.embeddeddb;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class EmbeddedTest {
    EmbeddedDatabase db;
    NamedParameterJdbcTemplate template;

    @Before
    public void setUp(){
        // 테이블 생성과 초기 데이터를 넣기 위한 스크립트 지정
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:sql/schema.sql")
                .addScript("classpath:sql/data.sql")
                .build();

        template = new NamedParameterJdbcTemplate(db);
    }

    @After
    public void tearDown(){
        db.shutdown();
    }

    @Test
    public void initData(){     // 초기화 스크립트를 통해 등록된 데이터를 검증하는 테스트
        assertThat(template.queryForObject("SELECT COUNT(*) FROM SQLMAP", (Map<String, ?>) null, Integer.class ), is(2));

        List<Map<String, Object>> list = template.queryForList("SELECT * FROM SQLMAP ORDER BY key_", (Map<String, ?>) null);

        assertThat(list.get(0).get("key_"), is("KEY1"));
        assertThat(list.get(0).get("sql_"), is("SQL1"));
        assertThat(list.get(1).get("key_"), is("KEY2"));
        assertThat(list.get(1).get("sql_"), is("SQL2"));
    }

    @Test
    public void insert(){
        Map<String, String> params = new HashMap<>();

        params.put("key_","KEY3");
        params.put("sql_","SQL3");
        template.update("INSERT INTO SQLMAP(key_, sql_) VALUES(:key_,:sql_)", params);

        assertThat(template.queryForObject("SELECT COUNT(*) FROM SQLMAP", (Map<String, ?>) null, Integer.class), is(3));
    }
}
