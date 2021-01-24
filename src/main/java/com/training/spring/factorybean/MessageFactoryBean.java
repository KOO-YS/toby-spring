package com.training.spring.factorybean;

import org.springframework.beans.factory.FactoryBean;


public class MessageFactoryBean implements FactoryBean<Message> {
    String text;

    // 오브젝트를 생성할 때 필요한 정보를 팩토리 빈의 프로퍼티로 설정해서 대신 DI 주입
    public void setText(String text) {
        this.text = text;
    }

    // 실제 빈으로 사용될 오브젝트를 직접 생성
    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(this.text);
    }

    @Override
    public Class<?> getObjectType() {
        return Message.class;
    }

    // ?? 
    // 팩토리 빈은 매번 요청할 때마다 새로운 오브젝트를 만들므로 false로 설정
    @Override
    public boolean isSingleton() {
        return false;
    }
}
