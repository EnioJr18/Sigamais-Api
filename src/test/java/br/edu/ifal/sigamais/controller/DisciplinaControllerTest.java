package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.DisciplinaRequestDTO;
import br.edu.ifal.sigamais.dto.DisciplinaResponseDTO;
import br.edu.ifal.sigamais.service.DisciplinaService;
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
class DisciplinaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DisciplinaService disciplinaService;

    @InjectMocks
    private DisciplinaController disciplinaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc de forma isolada
        mockMvc = MockMvcBuilders.standaloneSetup(disciplinaController).build();
    }

    @Test
    @DisplayName("POST /disciplinas - Deve cadastrar disciplina e retornar status 201 Created")
    void deveCadastrarDisciplinaComSucesso() throws Exception {
        // Arrange
        // Ajuste os construtores de acordo com os atributos reais do seu record/classe
        DisciplinaRequestDTO requestDTO = new DisciplinaRequestDTO("Programação Orientada a Objetos", 60);
        DisciplinaResponseDTO responseDTO = new DisciplinaResponseDTO(1, "Programação Orientada a Objetos", 60);

        when(disciplinaService.salvar(any(DisciplinaRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated()) // Valida se o Spring retornou 201 Created
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Programação Orientada a Objetos"));

        // Verifica se o controller chamou o service
        verify(disciplinaService, times(1)).salvar(any(DisciplinaRequestDTO.class));
    }

    @Test
    @DisplayName("GET /disciplinas - Deve listar todas as disciplinas e retornar status 200 OK")
    void deveListarDisciplinasComSucesso() throws Exception {
        // Arrange
        DisciplinaResponseDTO disciplinaMock = new DisciplinaResponseDTO(1, "Matemática Discreta", 80);

        when(disciplinaService.listarTodas()).thenReturn(List.of(disciplinaMock));

        // Act & Assert
        mockMvc.perform(get("/disciplinas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Valida se o Spring retornou 200 OK
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Matemática Discreta"));

        verify(disciplinaService, times(1)).listarTodas();
    }
}