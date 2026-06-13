package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.DadosAutenticacao;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutenticacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private br.edu.ifal.sigamais.security.TokenService tokenService;

    @MockitoBean
    private br.edu.ifal.sigamais.repository.UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager manager;

    @Test
    void deveRetornarTokenAoLogarComSucesso() throws Exception {
        DadosAutenticacao dados = new DadosAutenticacao("enio@ifal.edu.br", "123456");
        String jsonRequest = objectMapper.writeValueAsString(dados);

        Usuario usuarioFalso = new Usuario();
        usuarioFalso.setEmail("enio@ifal.edu.br");

        Authentication authFalsa = new UsernamePasswordAuthenticationToken(usuarioFalso, null);

        Mockito.when(manager.authenticate(any())).thenReturn(authFalsa);
        Mockito.when(tokenService.gerarToken(any(Usuario.class))).thenReturn("meu.token.jwt.falso");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("meu.token.jwt.falso"));
    }
}