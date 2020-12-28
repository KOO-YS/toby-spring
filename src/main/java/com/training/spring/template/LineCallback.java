package com.training.spring.template;

public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);    // 결과의 타입을 다양하게 이용
}
