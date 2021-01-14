package com.training.spring.util;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * 메일을 직접 전송하지 않고 테스트로만 사용할 것이니 빈 구현으로만 냅두자
 * DummyMailSender를 이용하는 한, 메일이 메일 서버로 발송될 일이 없다
 */
public class DummyMailSender implements MailSender {
    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {

    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }
}
