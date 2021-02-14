package com.training.spring.exception;

public class SqlRetrievalFailureException extends RuntimeException {
    public SqlRetrievalFailureException() {
    }

    public SqlRetrievalFailureException(String message) {
        super(message);
    }

    /**
     * @param cause SQL을 가져오는 데 실패한 근본 원인을 담을 수 있도록 중첩예외를 저장할 수 있는 생성자를 만들어 둔다
     */
    public SqlRetrievalFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}

