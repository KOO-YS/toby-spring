package com.training.spring;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 학습 테스트 진행
 */
public class JUnitTest {
    static JUnitTest testObject;
    static Set<JUnitTest> testObjectSets;
    // 테스트 :  현재 스태틱 변수에 담긴 오브젝트와 자신을 비교해서 같지 않다는 사실
    @Test
    public void test1(){
        // not() : 뒤에 나오는 결과를 부정하는 매처
        // sameInstance() :실제로 같은 오브젝트인지를 비교(값 동일성)
        assertThat(this, is(not(sameInstance(testObject))));
    }

    // 테스트 : 매번 새로운 오브젝트가 만들어지기 때문에 컬렉션 안에 중복되는 원소가 들어가지 않는다는 사실
    @Test
    public void test2(){
        // hasItem() : 컬렉션의 원소인지를 검사
        assertThat(testObjectSets, not(hasItem(this)));
    }
}
