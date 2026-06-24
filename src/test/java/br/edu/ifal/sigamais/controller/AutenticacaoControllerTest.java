package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.DadosAutenticacao;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AutenticacaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager manager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AutenticacaoController autenticacaoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc de forma isolada
        mockMvc = MockMvcBuilders.standaloneSetup(autenticacaoController).build();
    }

    @Test
    @DisplayName("POST /auth/login - Deve autenticar e retornar 200 OK com o Token JWT")
    void deveEfetuarLoginComSucesso() throws Exception {
        // Arrange
        DadosAutenticacao dadosLogin = new DadosAutenticacao("aluno@ifal.edu.br", "senha123");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setEmail("aluno@ifal.edu.br");
        usuarioMock.setNome("Enio Jr");

        // Precisamos mockar a resposta da interface Authentication do Spring Security
        Authentication authenticationMock = mock(Authentication.class);

        // Quando o manager tentar autenticar, devolvemos o mock de sucesso
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticationMock);

        // Quando o controller pedir o usuário autenticado (getPrincipal), devolvemos nosso usuarioMock
        when(authenticationMock.getPrincipal()).thenReturn(usuarioMock);

        // Quando pedir para gerar o token, devolvemos uma string falsa
        when(tokenService.gerarToken(usuarioMock)).thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mockToken");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosLogin)))
                .andExpect(status().isOk())
                // Ajuste "token" abaixo se o nome do atributo no seu record DadosTokenJWT for diferente
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mockToken"));

        // Verifica se os métodos foram chamados
        verify(manager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).gerarToken(usuarioMock);
    }

    @Test
    @DisplayName("POST /auth/login - Deve falhar e repassar exceção se credenciais forem inválidas")
    void deveFalharComCredenciaisInvalidas() throws Exception {
        // Arrange
        DadosAutenticacao dadosLogin = new DadosAutenticacao("aluno@ifal.edu.br", "senha_errada");

        // Quando o manager tentar autenticar uma senha errada, ele dispara uma BadCredentialsException
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dadosLogin)));
        });

        assertInstanceOf(BadCredentialsException.class, exception.getCause());

        // Garante que se a senha for errada, o TokenService NUNCA deve ser chamado
        verify(tokenService, never()).gerarToken(any(Usuario.class));
    }
}