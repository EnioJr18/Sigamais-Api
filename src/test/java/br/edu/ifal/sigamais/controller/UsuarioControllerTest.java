package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.AlterarSenhaDTO;
import br.edu.ifal.sigamais.dto.UsuarioPerfilDTO;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import br.edu.ifal.sigamais.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    // Objeto para converter nossos DTOs em JSON para os testes simularem o Front-end
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc apenas com este Controller, sem carregar o Spring Security
        // Isso permite testar as rotas de forma isolada e rápida
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    @DisplayName("GET /usuarios/me - Deve retornar 200 OK e o perfil do usuário, incluindo a flag emRisco")
    void deveRetornarMeuPerfilComSucesso() throws Exception {
        // Arrange
        UsuarioPerfilDTO perfilMock = new UsuarioPerfilDTO(
                1, "Enio Jr", "12345678900", "aluno@ifal.edu.br",
                "ALUNO", "999999999", "Rua A", "foto.jpg", true
        );
        when(usuarioService.obterMeuPerfil()).thenReturn(perfilMock);

        // Act & Assert
        mockMvc.perform(get("/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Enio Jr"))
                .andExpect(jsonPath("$.perfil").value("ALUNO"))
                .andExpect(jsonPath("$.emRisco").value(true)); // Valida se a nossa trava de segurança está indo no JSON
    }

    @Test
    @DisplayName("PUT /usuarios/me/senha - Deve retornar 204 No Content ao alterar a senha com sucesso")
    void deveAlterarSenhaComSucesso() throws Exception {
        // Arrange
        AlterarSenhaDTO dto = new AlterarSenhaDTO("senhaAtualCerta", "novaSenha123");

        // Como o método alterarMinhaSenha é 'void', não precisamos de um 'when(...).thenReturn(...)'.
        // Apenas chamamos a rota. O Mockito já entende que ele deve rodar liso.

        // Act & Assert
        mockMvc.perform(put("/usuarios/me/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent()); // Espera o status 204

        // Verifica se o controller repassou o comando para o service
        verify(usuarioService, times(1)).alterarMinhaSenha(any(AlterarSenhaDTO.class));
    }

    @Test
    @DisplayName("PUT /usuarios/me/senha - Deve retornar 400 Bad Request se a senha atual estiver errada")
    void deveRetornarBadRequestQuandoSenhaAtualIncorreta() throws Exception {
        // Arrange
        AlterarSenhaDTO dto = new AlterarSenhaDTO("senhaAtualErrada", "novaSenha");

        // Simulamos o service estourando a exceção que programamos
        doThrow(new IllegalArgumentException("A senha atual informada está incorreta."))
                .when(usuarioService).alterarMinhaSenha(any(AlterarSenhaDTO.class));

        // Act & Assert
        mockMvc.perform(put("/usuarios/me/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()) // Espera o status 400
                .andExpect(content().string("A senha atual informada está incorreta.")); // O React recebe exatamente esta string!
    }
}