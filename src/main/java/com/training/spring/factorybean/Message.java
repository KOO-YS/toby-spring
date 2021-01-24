package com.training.spring.factorybean;

public class Message {

    private String text;

    // 외부에서 생성자를 통해 오브젝트를 만들 수 없다
    private Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    // 생성자 대신 사용할 수 있는 스태틱 팩토리 메소드 제공
    public static Message newMessage(String text) {
        return new Message(text);
    }
}
