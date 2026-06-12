package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.ProfessorRequestDTO;
import br.edu.ifal.sigamais.dto.ProfessorResponseDTO;
import br.edu.ifal.sigamais.service.ProfessorService;
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

@WebMvcTest(ProfessorController.class)
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfessorService professorService;

    @Test
    void deveCadastrarProfessorComSucesso() throws Exception {
        String jsonRequest = """
                {
                    "usuarioId": 1,
                    "titulacao": "Doutor"
                }
                """;

        ProfessorResponseDTO responseDTO = new ProfessorResponseDTO(1, "Doutor");

        Mockito.when(professorService.salvar(any(ProfessorRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/professores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulacao").value("Doutor"));
    }
}