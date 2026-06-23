package br.edu.ifal.sigamais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarEmailAlerta(String para, String assunto, String texto) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(para);
        mensagem.setFrom("eniojunior111@gmail.com");
        mensagem.setSubject(assunto);
        mensagem.setText(texto);

        mailSender.send(mensagem);
    }
}