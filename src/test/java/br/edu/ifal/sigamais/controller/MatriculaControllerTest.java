package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.service.AnaliseRiscoService;
import br.edu.ifal.sigamais.service.MatriculaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MatriculaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private br.edu.ifal.sigamais.security.TokenService tokenService;

    @Mock
    private br.edu.ifal.sigamais.repository.UsuarioRepository usuarioRepository;

    @Mock
    private AnaliseRiscoService analiseRiscoService;

    @Mock
    private MatriculaService matriculaService;

    @InjectMocks
    private MatriculaController matriculaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(matriculaController).build();
    }

    @Test
    void deveRealizarMatriculaComSucesso() throws Exception {
        MatriculaRequestDTO requestDTO = new MatriculaRequestDTO(3, 2);
        MatriculaResponseDTO responseDTO = new MatriculaResponseDTO(1, 123366, "Enio Jr", "20260001", 13356, "2026.1", 2026, "Estrutura de Dados", "Charles Xavier");

        Mockito.when(matriculaService.realizarMatricula(any(MatriculaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alunoMatricula").value("20260001"))
                .andExpect(jsonPath("$.disciplinaNome").value("Estrutura de Dados"))
                .andExpect(jsonPath("$.alunoNome").value("Enio Jr"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 200 e o nível de risco calculado para a matrícula")
    void deveRetornarNivelDeRiscoComSucesso() throws Exception {

        // Arrange: cria um DTO de alerta com risco ALTO
        br.edu.ifal.sigamais.dto.AlertaRiscoDTO alerta = new br.edu.ifal.sigamais.dto.AlertaRiscoDTO(
                "ALTO",
                java.math.BigDecimal.valueOf(4.5),
                15,
                java.util.List.of("Muitas faltas")
        );

        Mockito.when(analiseRiscoService.analisarRiscoMatricula(1)).thenReturn(alerta);

        mockMvc.perform(get("/matriculas/1/risco")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera HTTP 200
                .andExpect(jsonPath("$.risco").value("ALTO"));
    }
}