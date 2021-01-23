package com.training.spring.proxy;

/**
 * 타깃 오브젝트의 메소드를 호출한 뒤에 위임과 부가 기능을 추가할 프록시 클래스 
 */
public class HelloUppercase implements Hello{
    Hello hello;        // 위임할 타깃 오브젝트
                        // -> 다른 프록시에게 다시 위임할 수 있으므로, 인터페이스로 선언한다

    public HelloUppercase(Hello hello) {
        this.hello = hello;
    }

    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();      // 위임 + 부가기능 적용
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}
