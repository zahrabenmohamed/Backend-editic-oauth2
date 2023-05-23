package net.codeslate.keycloak.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {


    private JavaMailSender javaMailSender;

    public void sendEmail(String toEmail,String subject , String body){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("zahra.benmohamed.etu20@ensem.ac.ma");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        javaMailSender.send(message);
        System.out.println("Mail sent successfully");

    }
}
