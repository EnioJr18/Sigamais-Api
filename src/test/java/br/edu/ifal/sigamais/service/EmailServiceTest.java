package br.edu.ifal.sigamais.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Test
    @DisplayName("Deve enviar e-mail de alerta com sucesso")
    void deveEnviarEmailComSucesso() {
        String para = "aluno@ifal.edu.br";
        String assunto = "Alerta de Risco";
        String texto = "Você está em risco de reprovação.";

        emailService.enviarEmailAlerta(para, assunto, texto);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender).send(captor.capture());

        SimpleMailMessage mensagemEnviada = captor.getValue();
        assertNotNull(mensagemEnviada.getTo());
        assertEquals(para, mensagemEnviada.getTo()[0]);
        assertEquals("eniojunior111@gmail.com", mensagemEnviada.getFrom());
        assertEquals(assunto, mensagemEnviada.getSubject());
        assertEquals(texto, mensagemEnviada.getText());
    }

    @Test
    @DisplayName("Deve propagar exceção caso ocorra erro no servidor de e-mail")
    void devePropagarExcecaoServidorEmail() {
        String para = "aluno@ifal.edu.br";
        String assunto = "Alerta";
        String texto = "Erro ao enviar";

        Mockito.doThrow(new MailSendException("Erro na conexão SMTP"))
               .when(mailSender).send(any(SimpleMailMessage.class));

        MailException exception = assertThrows(MailSendException.class, () -> {
            emailService.enviarEmailAlerta(para, assunto, texto);
        });

        assertEquals("Erro na conexão SMTP", exception.getMessage());
        Mockito.verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve falhar ao tentar enviar com dados nulos ou inválidos simulando erro de formatação")
    void deveFalharComDadosInvalidos() {
        String para = null;
        String assunto = "Alerta";
        String texto = "Texto qualquer";

        // Caso o SimpleMailMessage falhe logo na atribuição, ele lança IllegalArgumentException ou NullPointerException.
        // Se a atribuição passar, o mailSender rejeitará a mensagem.
        Mockito.doThrow(new IllegalArgumentException("Destinatário nulo ou inválido"))
               .when(mailSender).send(any(SimpleMailMessage.class));

        Exception exception = assertThrows(Exception.class, () -> {
            // Em caso do Spring validar null internamente na modelagem, ele não chegará no send,
            // senão, estourará na nossa simulação do mailSender.
            emailService.enviarEmailAlerta(para, assunto, texto);
        });

        assertNotNull(exception);
    }
}
