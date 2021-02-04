package com.training.spring.proxy;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Proxy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest
public class DynamicProxyTest {

    @Test
    public void simpleProxy(){
        // JDK 다이나믹 프록시 생성
        com.training.spring.proxy.Hello proxiedHello = (com.training.spring.proxy.Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{com.training.spring.proxy.Hello.class},
                new UppercaseHandler(new com.training.spring.proxy.HelloTarget())
        );

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    @Test
    public void proxyFactoryBean(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());            // 타깃 설정
        pfBean.addAdvice(new UppercaseAdvice());        // 부가 기능을 담은 어드바이스 추가 (여러 개 가능)
        
        // FactoryBean이므로 getObject()로 생성된 프록시를 가져온다
        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            // 리플렉션의 Method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요가 없다
            String result = (String)invocation.proceed();       // Proceed to the next interceptor in the chain.
            // MethodInvocation은 메소드 정보와 함께 타깃 오브젝트를 알고있다!
            return result.toUpperCase();        // 부가 기능 적용
        }
    }

//    static interface Hello {        // target & proxy가 구현할 인터페이스
//        String sayHello(String name);
//        String sayHi(String name);
//        String sayThankYou(String name);
//    }
//
//    static class HelloTarget implements Hello{
//        @Override
//        public String sayHello(String name) {
//            return "Hello "+name;
//        }
//
//        @Override
//        public String sayHi(String name) {
//            return "Hi "+name;
//        }
//
//        @Override
//        public String sayThankYou(String name) {
//            return "Thank You "+name;
//        }
//    }

    @Test
    public void pointcutAdvisor(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        // 메소드 이름을 비교해서 대상을 선정하는 알고리즘 제공 포인트컷
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
    }

    @Test
    public void classNamePointcutAdvisor(){
        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
            @Override
            public ClassFilter getClassFilter() {       // 익명 내부 클래스 방식으로 클래스 정의
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT");      // HelloT로 시작하는 "클래스" 이름만 선정
                    }
                };
            }
        };
        classMethodPointcut.setMappedName("sayH*");     // sayH로 시작하는 이름을 가진 "메소드"만 선정

        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget{};         // 적용 X
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

        class HelloToby extends HelloTarget{};          // 적용 O
        checkAdviced(new HelloToby(), classMethodPointcut, true);

    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if(adviced){
            // 메소드 선정 방식을 통해 선정된 어드바이스 적용
            assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
            assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
            // --- end
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        } else {
            // 어드바이스 적용 대상 후보에서 아예 탈락
            assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
            assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }
    }
}
