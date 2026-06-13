package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.DisciplinaRequestDTO;
import br.edu.ifal.sigamais.dto.DisciplinaResponseDTO;
import br.edu.ifal.sigamais.service.DisciplinaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DisciplinaController.class)
@AutoConfigureMockMvc(addFilters = false)
class DisciplinaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private br.edu.ifal.sigamais.security.TokenService tokenService;

    @MockitoBean
    private br.edu.ifal.sigamais.repository.UsuarioRepository usuarioRepository;

    @MockitoBean
    private DisciplinaService disciplinaService;

    @Test
    void deveCadastrarDisciplinaComSucesso() throws Exception {
        String jsonRequest = """
                {
                    "nome": "Banco de Dados",
                    "cargaHoraria": 60
                }
                """;

        DisciplinaResponseDTO responseDTO = new DisciplinaResponseDTO(1, "Banco de Dados", 60);

        Mockito.when(disciplinaService.salvar(any(DisciplinaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Banco de Dados"))
                .andExpect(jsonPath("$.cargaHoraria").value(60));
    }
}