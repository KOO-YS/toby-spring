package com.training.spring.factorybean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// https://www.baeldung.com/spring-factorybean
@Configuration
public class FactoryBeanConfig {

    @Bean
    public Message message() throws Exception {
        MessageFactoryBean message = new MessageFactoryBean();
        message.setText("Factory Bean");
        return message.getObject();     // 빈의 오브젝트 타입이 MessageFactoryBean가 아닌 Message
    }

}
