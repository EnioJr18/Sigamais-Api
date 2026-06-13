package br.edu.ifal.sigamais.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.service.TurmaService;

@WebMvcTest(TurmaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TurmaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private br.edu.ifal.sigamais.security.TokenService tokenService;

    @MockitoBean
    private br.edu.ifal.sigamais.repository.UsuarioRepository usuarioRepository;

    @MockitoBean
    private TurmaService turmaService;

    @Test
    public void deveRetornarStatus404QuandoProfessorNaoExistir() throws Exception {
        // Cenário: Mensagem exata que o seu service lança
        when(turmaService.salvar(any(), any(), any(), any())) // Corrigido de criarTurma para salvar
            .thenThrow(new RecursoNaoEncontradoException("Professor não encontrado com o ID: 99"));

        // Ação e Validação
        mockMvc.perform(post("/api/turmas")
                .param("professorId", "99")
                .param("disciplinaId", "1")
                .param("semestre", "2026.1")
                .param("ano", "2026"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Professor não encontrado com o ID: 99")); // Valida o campo "erro" do seu curl
    }
}