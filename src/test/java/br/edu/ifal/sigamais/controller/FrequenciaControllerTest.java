package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.FrequenciaRequestDTO;
import br.edu.ifal.sigamais.dto.FrequenciaResponseDTO;
import br.edu.ifal.sigamais.dto.FrequenciaResumoDTO;
import br.edu.ifal.sigamais.service.FrequenciaService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FrequenciaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FrequenciaService frequenciaService;

    @InjectMocks
    private FrequenciaController frequenciaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(frequenciaController).build();
    }

    @Test
    @DisplayName("POST /frequencias - Deve registrar frequência e retornar status 201 Created")
    void deveRegistrarFrequenciaComSucesso() throws Exception {
        FrequenciaRequestDTO requestDTO = new FrequenciaRequestDTO(1, 2);
        FrequenciaResponseDTO responseDTO = new FrequenciaResponseDTO(1L, 1, 2);

        when(frequenciaService.registrarFrequencia(any(FrequenciaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/frequencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(frequenciaService, times(1)).registrarFrequencia(any(FrequenciaRequestDTO.class));
    }

    @Test
    @DisplayName("GET /frequencias/matriculas/{matriculaId} - Deve listar frequências por matrícula com status 200 OK")
    void deveListarFrequenciasPorMatricula() throws Exception {
        FrequenciaResponseDTO responseDTO = new FrequenciaResponseDTO(1L, 1, 2);

        when(frequenciaService.listarFrequenciaPorMatricula(1)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/frequencias/matriculas/{matriculaId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(frequenciaService, times(1)).listarFrequenciaPorMatricula(1);
    }

    @Test
    @DisplayName("GET /frequencias/matriculas/{matriculaId}/status-reprovacao - Deve retornar status de reprovação com 200 OK")
    void deveVerificarStatusReprovacao() throws Exception {
        when(frequenciaService.verificarReprovacaoPorFalta(1)).thenReturn(true);

        mockMvc.perform(get("/frequencias/matriculas/{matriculaId}/status-reprovacao", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(frequenciaService, times(1)).verificarReprovacaoPorFalta(1);
    }

    @Test
    @DisplayName("GET /frequencias - Deve listar todas as frequências com status 200 OK")
    void deveListarTodasFrequencias() throws Exception {
        FrequenciaResponseDTO responseDTO = new FrequenciaResponseDTO(1L, 1, 2);

        when(frequenciaService.listarTodasFrequencias()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/frequencias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(frequenciaService, times(1)).listarTodasFrequencias();
    }

    @Test
    @DisplayName("GET /frequencias/resumo - Deve listar o resumo de frequências com status 200 OK")
    void deveListarResumoFrequencias() throws Exception {
        FrequenciaResumoDTO resumoDTO = new FrequenciaResumoDTO(1, "Enio Jr", "2026001", "POO", "Professor", "2026.1", 10, 5);

        when(frequenciaService.listarResumoFrequencias()).thenReturn(List.of(resumoDTO));

        mockMvc.perform(get("/frequencias/resumo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matriculaId").value(1))
                .andExpect(jsonPath("$[0].alunoNome").value("Enio Jr"));

        verify(frequenciaService, times(1)).listarResumoFrequencias();
    }
}