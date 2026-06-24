package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.NotaDetalheDTO;
import br.edu.ifal.sigamais.dto.NotaRequestDTO;
import br.edu.ifal.sigamais.dto.NotaResponseDTO;
import br.edu.ifal.sigamais.dto.NotaResumoDTO;
import br.edu.ifal.sigamais.service.NotaService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotaService notaService;

    @InjectMocks
    private NotaController notaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notaController).build();
    }

    @Test
    @DisplayName("POST /notas - Deve cadastrar nota e retornar status 201 Created")
    void deveCadastrarNotaComSucesso() throws Exception {
        NotaRequestDTO requestDTO = new NotaRequestDTO(1, BigDecimal.valueOf(8.5), "PROVA");
        NotaResponseDTO responseDTO = new NotaResponseDTO(1, 1, BigDecimal.valueOf(8.5), "PROVA");

        when(notaService.cadastrarNota(any(NotaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("PROVA"));

        verify(notaService, times(1)).cadastrarNota(any(NotaRequestDTO.class));
    }

    @Test
    @DisplayName("GET /notas/matriculas/{matriculaId} - Deve listar notas por matrícula com status 200 OK")
    void deveListarNotasPorMatricula() throws Exception {
        NotaResponseDTO responseDTO = new NotaResponseDTO(1, 1, BigDecimal.valueOf(8), "AV1");

        when(notaService.listarNotasPorMatricula(1)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/notas/matriculas/{matriculaId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].matriculaId").value(1));

        verify(notaService, times(1)).listarNotasPorMatricula(1);
    }

    @Test
    @DisplayName("GET /notas/matriculas/{matriculaId}/status-media - Deve retornar status de aprovação com 200 OK")
    void deveVerificarAprovacaoMedia() throws Exception {
        when(notaService.verificarAprovacaoPorMedia(1)).thenReturn(true);

        mockMvc.perform(get("/notas/matriculas/{matriculaId}/status-media", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(notaService, times(1)).verificarAprovacaoPorMedia(1);
    }

    @Test
    @DisplayName("GET /notas - Deve listar todas as notas com status 200 OK")
    void deveListarTodasNotas() throws Exception {
        NotaResponseDTO responseDTO = new NotaResponseDTO(1, 1, BigDecimal.valueOf(8.5), "PROVA");

        when(notaService.listarTodasNotas()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/notas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(notaService, times(1)).listarTodasNotas();
    }

    @Test
    @DisplayName("GET /notas/resumo - Deve listar o resumo de notas com status 200 OK")
    void deveListarResumoNotas() throws Exception {
        NotaDetalheDTO detalhe = new NotaDetalheDTO(1, "PROVA", BigDecimal.valueOf(8.5));
        NotaResumoDTO resumoDTO = new NotaResumoDTO(
                1, "Enio Jr", "2026001", "POO", "Professor",
                "2026.1", BigDecimal.valueOf(8.5), 1, "APROVADO", List.of(detalhe)
        );

        when(notaService.listarResumoNotas()).thenReturn(List.of(resumoDTO));

        mockMvc.perform(get("/notas/resumo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matriculaId").value(1))
                .andExpect(jsonPath("$[0].alunoNome").value("Enio Jr"))
                .andExpect(jsonPath("$[0].notas[0].valor").value(8.5));

        verify(notaService, times(1)).listarResumoNotas();
    }
}