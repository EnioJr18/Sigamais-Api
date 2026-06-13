package br.edu.ifal.sigamais.security;

import br.edu.ifal.sigamais.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "senha-super-secreta-de-teste");
    }

    @Test
    void deveGerarEValidarTokenComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@ifal.edu.br");

        String token = tokenService.gerarToken(usuario);

        Assertions.assertNotNull(token);

        String subject = tokenService.getSubject(token);

        Assertions.assertEquals("admin@ifal.edu.br", subject);
    }
}