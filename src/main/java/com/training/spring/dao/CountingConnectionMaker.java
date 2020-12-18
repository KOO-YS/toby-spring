package com.training.spring.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DAO와 DB 커넷션을 만드는 오브젝트 사이에서 연결 횟수를 카운트 하는 오브젝트
 *
 * ConnectionMaker 인터페이스를 구현했지만 내부에서 직접 DB 커넥션을 만들지 않는다
 * But, DAO가 DB 커넥션을 가져올 때마다 호출하는 makeNewConnection()에서 카운터를 증가시킨다
 */
public class CountingConnectionMaker implements ConnectionMaker{
    int counter;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return realConnectionMaker.makeNewConnection();     // 실제 사용할 DB커넥션을 제공해주는 오브젝트를 통해 들어감
                                                            // UserDAO -> CountingConnectionMaker -> DConnectionMaker (새로운 의존 관계)
    }
}
