package com.training.spring.proxy;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest
public class ReflectionTest {
    @Test
    public void invokeMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String name = "Spring";

        // length()
        assertThat(name.length(), is(6));

        /**
         * Method ? 리플렉션 API 중에서 메소드에 대한 정의를 담은 인터페이스
         *          1. 메소드에 대한 자세한 정보를 담고 있음
         *          2. 특정 오브젝트의 메소드를 실행시킬 수 있다
         *          -> invoke() 메소드 사용
         */
        Method lengthMethod = String.class.getMethod("length");
        assertThat((Integer)lengthMethod.invoke(name)
        //                               invoke(메소드를 실행시킬 타깃 오브젝트)
                                        , is(6));

        // charAt()
        assertThat(name.charAt(0), is('S'));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character)charAtMethod.invoke(name, 0),
        //                                 invoke(메소드를 실행시킬 타깃 오브젝트, 들어갈 파라미터)
                                        is('S'));
    }

    @Test
    public void simpleProxy(){
        Hello hello = new HelloTarget();        // 타깃은 인터페이스를 통해 접근하는 것이 좋다
        assertThat(hello.sayHello("Toby"), is("Hello Toby"));
        assertThat(hello.sayHi("Toby"), is("Hi Toby"));
        assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));

        // proxy applied
        Hello proxieHello = new HelloUppercase(new HelloTarget());      // 프록시를 통해 타깃 오브젝트에 접근한다
        assertThat(proxieHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxieHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxieHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
        /* FIXME :  문제점을 가진 코드
        *  문제점 1. 인터페이스의 모든 메소드를 구현해 위임하는 코드 만듦
        *  문제점 2. 부가 기능(리턴 값을 태문자로 바꾸는 기능)이 모든 메소드에서 중복
        */
    }

    @Test
    public void dynamicProxy(){
        /**
         * Object newProxyInstance(ClassLoader loader,
         *                          Class<?>[] interfaces,
         *                          InvocationHandler h)
         */
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),                // 동적으로 생성되는 다이나믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[]{Hello.class},                   // 구현할 인터페이 스
                new UppercaseHandler((new HelloTarget()))   // 부가 기능과 위임 코드를 담은 InvocationHandler
        );

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }
}
