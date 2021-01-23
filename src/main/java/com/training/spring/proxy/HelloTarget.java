package com.training.spring.proxy;

/**
 * 프록시를 적용할 간단한 타깃 클래스
 */
public class HelloTarget implements Hello{

    @Override
    public String sayHello(String name) {
        return "Hello "+name;
    }

    @Override
    public String sayHi(String name) {
        return "Hi "+name;
    }

    @Override
    public String sayThankYou(String name) {
        return "Thank You "+name;
    }
}
