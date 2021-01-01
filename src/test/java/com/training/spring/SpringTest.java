package com.training.spring;

import com.training.spring.dao.UserDaoJdbc;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UserDaoJdbc.class)
public class SpringTest {


    // 테스트 조건 :  테스트 컨텍스트가 매번 주입하는 애플리케이션 컨텍스트는 항상 같은 오브젝트
    @Autowired
    ApplicationContext context;

    static ApplicationContext contextObject = null;

    @Test
    public void test1(){
         /*
             contextObject가 null이거나,  현재 context와 같거나
             결과값을 True와 비교했을 때 일치 여부
         */
        assertThat(contextObject == null || contextObject == this.context, is(Boolean.TRUE));
        contextObject = this.context;
    }
    @Test
    public void test2(){
        /*
             contextObject가 null이거나,  현재 context와 같거나
             반환하는 결과가 True인지 확인
         */
        assertTrue(contextObject == null || contextObject == this.context);
        contextObject = this.context;
    }
    @Test
    public void test3(){
        /*
             null 값 또는 현재 context가
             contextObject와 비교했을 때 일치 여부
         */
        assertThat(contextObject, either(is(nullValue())).or(is(this.context)));
        contextObject = this.context;
    }
}
