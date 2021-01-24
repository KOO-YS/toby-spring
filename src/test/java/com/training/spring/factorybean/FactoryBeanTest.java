package com.training.spring.factorybean;

import com.training.spring.factory.BeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@SpringBootTest
public class FactoryBeanTest {

    @Autowired
    Message message;

    @Before
    public void setUp(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FactoryBeanConfig.class);
        message = context.getBean("message", Message.class);
    }

    @Test
    public void getMessageFromFactoryBean(){
        // 타입 확인
        assertThat(message.getClass(), is(Message.class));
        // 설정과 기능 확인
        assertEquals("Factory Bean", message.getText());
    }
}
