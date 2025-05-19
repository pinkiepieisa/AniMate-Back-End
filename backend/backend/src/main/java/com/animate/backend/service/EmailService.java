package com.animate.backend.service;

import com.animate.backend.dio.MailBody;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender
                        javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    //Responsável por enviar o e-mail

    public void sendSimpleMessage(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom("animate.gmail.com");
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());
        //definição das propriedades do e-mail

        javaMailSender.send(message);

        //Função que auxília no envio do e-mail
    }
}
