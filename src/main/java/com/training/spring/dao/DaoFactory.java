package com.training.spring.dao;

// 오브젝트의 생성 방법을 결정하고 만들어줄 클래스
// -> UserDao 생성 담당
// -> IoC 컨테이너
public class DaoFactory {
    
    // Dao가 많아질 상황을 대비해 중복이 예상될 코드 분리 추출 후 메소드화
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }

    public UserDao userDao(){
        return new UserDao(connectionMaker());      // 이 메소드 안에서 connection 정보를 바꿀 일이 없어졌다
    }
}
