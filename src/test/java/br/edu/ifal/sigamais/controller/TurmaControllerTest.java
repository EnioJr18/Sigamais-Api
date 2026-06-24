package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.TurmaRequestDTO;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.repository.TurmaRepository;
import br.edu.ifal.sigamais.service.TurmaService;
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
class TurmaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TurmaService turmaService;

    @Mock
    private TurmaRepository turmaRepository;

    @InjectMocks
    private TurmaController turmaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(turmaController).build();
    }

    @Test
    @DisplayName("POST /turmas - Deve criar turma e retornar status 201 Created")
    void deveCriarTurmaComSucesso() throws Exception {
        // Ajuste os parâmetros do construtor do TurmaRequestDTO conforme sua implementação
        TurmaRequestDTO requestDTO = new TurmaRequestDTO(1, 1, "2026.1", 2026, 40);

        Turma turmaMock = new Turma();
        turmaMock.setId(1);
        turmaMock.setSemestre("2026.1");
        turmaMock.setAno(2026);

        when(turmaService.salvar(any(TurmaRequestDTO.class))).thenReturn(turmaMock);

        mockMvc.perform(post("/turmas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.semestre").value("2026.1"))
                .andExpect(jsonPath("$.ano").value(2026));

        verify(turmaService, times(1)).salvar(any(TurmaRequestDTO.class));
    }

    @Test
    @DisplayName("GET /turmas - Deve listar todas as turmas e retornar status 200 OK")
    void deveListarTurmasComSucesso() throws Exception {
        Turma turmaMock = new Turma();
        turmaMock.setId(1);
        turmaMock.setSemestre("2026.1");

        when(turmaRepository.findAll()).thenReturn(List.of(turmaMock));

        mockMvc.perform(get("/turmas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].semestre").value("2026.1"));

        verify(turmaRepository, times(1)).findAll();
    }
}