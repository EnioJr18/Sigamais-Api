package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.ProfessorRequestDTO;
import br.edu.ifal.sigamais.dto.ProfessorResponseDTO;
import br.edu.ifal.sigamais.service.ProfessorService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfessorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfessorService professorService;

    @InjectMocks
    private ProfessorController professorController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(professorController).build();
    }

    @Test
    @DisplayName("POST /professores - Deve cadastrar professor e retornar status 201 Created")
    void deveCadastrarProfessorComSucesso() throws Exception {
        ProfessorRequestDTO requestDTO = new ProfessorRequestDTO("Charles Xavier", "charlesxavier@ifal.edu.br", "12345678998", "charles123", "Mestre");
        ProfessorResponseDTO responseDTO = new ProfessorResponseDTO(1, "Charles Xavier", "charlesxavier@ifal.edu.br", "Mestre");

        when(professorService.salvar(any(ProfessorRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/professores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Charles Xavier"));

        verify(professorService, times(1)).salvar(any(ProfessorRequestDTO.class));
    }

    @Test
    @DisplayName("GET /professores - Deve listar todos os professores com status 200 OK")
    void deveListarProfessoresComSucesso() throws Exception {
        ProfessorResponseDTO responseDTO = new ProfessorResponseDTO(1, "Kenji Kamei", "kenjikamei@ifal.edu.br", "Doutorado");

        when(professorService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/professores")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Kenji Kamei"));

        verify(professorService, times(1)).listarTodos();
    }
}