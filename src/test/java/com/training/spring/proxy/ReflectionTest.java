package com.training.spring.proxy;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

}
