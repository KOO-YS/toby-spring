package com.training.spring.util;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;

public class MockMailSender implements MailSender {
    // UserService로부터 전송 요청을 받은 메일 주소를 저장해두고 이를 읽을 수 있게 한다
    private List<String> requests = new ArrayList<>();

    public List<String> getRequests(){
        return requests;
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        // 전송 요청 받은 이메일 주소를 저장해 둔다(간단히 첫 번째 수신자 메일 주소만 저장)
        requests.add(simpleMessage.getTo()[0]);
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }
}
