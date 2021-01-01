package com.training.spring.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

/**
 * 아이디 중복 시 사용하는 예외
 */
public class DuplicateUserIdException extends DuplicateKeyException {

    // 중첩 예외를 만들 수 있도록 생성자 추가
    public DuplicateUserIdException(Throwable cause) {
        super(cause.getMessage());   // 메시지 또는 예외 상황을 전달하는 데 필요한 정보를 더 넣을 수 있도록
    }
}
