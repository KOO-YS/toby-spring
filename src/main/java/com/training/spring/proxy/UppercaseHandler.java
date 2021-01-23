package com.training.spring.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {        // 다이나믹 프록시로부터 요청을 전달받기 위해 구현
    Hello target;

    // 다이나믹 프록시로부터 전달받은 요청을 다시 타깃 오브젝트에 위임해야 하기 때문에 타깃 오브젝트를 주입 받아 둔다
    public UppercaseHandler(Hello target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 타깃으로 위임. 인터페이스의 메소드 호출에 모두 적용된다
        String result = (String) method.invoke(target, args);
        return result.toUpperCase();        // 한꺼번에 부가 기능 제공
    }
}
