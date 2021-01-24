package com.training.spring.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {        // 다이나믹 프록시로부터 요청을 전달받기 위해 구현
    Object target;

    // 어떤 종류의 인터페이스를 구현한 타깃에도 적용가능하도록 Object 타입으로 수정
    public UppercaseHandler(Hello target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 타깃으로 위임. 인터페이스의 메소드 호출에 모두 적용된다
        Object result = method.invoke(target, args);
//        if(result instanceof String)        // 호출 메소드의 리턴 타입이 String일때만 부가 기능 추가 (대문자 변경 기능)
        if(result instanceof String && method.getName().startsWith("say"))
            return ((String) result).toUpperCase();
        else
            return result;
    }
}
