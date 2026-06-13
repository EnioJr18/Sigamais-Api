package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.service.AnaliseRiscoService;
import br.edu.ifal.sigamais.service.MatriculaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatriculaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatriculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private br.edu.ifal.sigamais.security.TokenService tokenService;

    @MockitoBean
    private br.edu.ifal.sigamais.repository.UsuarioRepository usuarioRepository;

    @MockitoBean
    private AnaliseRiscoService analiseRiscoService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MatriculaService matriculaService;

    @Test
    void deveRealizarMatriculaComSucesso() throws Exception {
        MatriculaRequestDTO requestDTO = new MatriculaRequestDTO(3, 2);
        MatriculaResponseDTO responseDTO = new MatriculaResponseDTO(1, "2024001", "Estrutura de Dados", "ATIVA");

        Mockito.when(matriculaService.realizarMatricula(any(MatriculaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matriculaAluno").value("2024001"))
                .andExpect(jsonPath("$.nomeDisciplina").value("Estrutura de Dados"))
                .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 200 e o nível de risco calculado para a matrícula")
    void deveRetornarNivelDeRiscoComSucesso() throws Exception {
        // Prepara: Ensina o mock a devolver "ALTO" quando consultarem a matrícula 1
        Mockito.when(analiseRiscoService.analisarRiscoGlobal(1)).thenReturn("ALTO");

        // Executa a requisição GET e verifica
        mockMvc.perform(get("/matriculas/1/risco")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.risco").value("ALTO"));
    }
}