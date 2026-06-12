package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.AlunoResponseDTO;
import br.edu.ifal.sigamais.service.AlunoService;
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

@WebMvcTest(AlunoController.class)
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlunoService alunoService;

    @Test
    void deveCadastrarAlunoComSucesso() throws Exception {
        String jsonRequest = """
                {
                    "usuarioId": 1,
                    "matricula": "2024001",
                    "curso": "Sistemas de Informação",
                    "rendaFamiliar": 2500.0,
                    "anoIngresso": 2024,
                    "status": "ATIVO"
                }
                """;

        AlunoResponseDTO responseDTO = new AlunoResponseDTO(1, "Enio Jr", "2024001", "Sistemas de Informação", "ATIVO");

        Mockito.when(alunoService.salvar(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("2024001"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }
}