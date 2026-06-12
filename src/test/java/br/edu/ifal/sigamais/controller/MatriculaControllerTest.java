package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.service.MatriculaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatriculaController.class)
class MatriculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}