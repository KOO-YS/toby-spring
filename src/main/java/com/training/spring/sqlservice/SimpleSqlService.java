package com.training.spring.sqlservice;

import com.training.spring.exception.SqlRetrievalFailureException;

import java.util.Map;

public class SimpleSqlService implements SqlService{
    private Map<String, String> sqlMap;

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if(sql == null){
            throw new SqlRetrievalFailureException(key+"에 대한 SQL을 찾을 수 없습니다");
            // 인터페이스에 정의된 규약대로 SQL을 가져오는 데 실패하면 예외
        }
        return sql;
    }
}
