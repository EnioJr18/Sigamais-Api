package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.AlertaResponseDTO;
import br.edu.ifal.sigamais.dto.AtualizarAlertaDTO;
import br.edu.ifal.sigamais.dto.HistoricoAlertaDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.enums.StatusAlerta;
import br.edu.ifal.sigamais.service.AlertaRiscoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AlertaRiscoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlertaRiscoService alertaRiscoService;

    @InjectMocks
    private AlertaRiscoController alertaRiscoController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc de forma isolada
        mockMvc = MockMvcBuilders.standaloneSetup(alertaRiscoController).build();
    }

    @Test
    @DisplayName("POST /alertas-risco/{matriculaId}/notificar - Deve retornar 200 OK com mensagem de sucesso")
    void deveNotificarCoordenacaoComSucesso() throws Exception {

        mockMvc.perform(post("/alertas-risco/{matriculaId}/notificar", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Coordenação notificada com sucesso."));

        // Verifica se o controller acionou o serviço no final
        verify(alertaRiscoService, times(1)).notificarCoordenacao(1);
    }

    @Test
    @DisplayName("POST /alertas-risco/{matriculaId}/notificar - Deve retornar 400 Bad Request se a validação do serviço falhar")
    void deveRetornarBadRequestAoFalharNotificacao() throws Exception {
        // Forçamos o serviço a estourar a exceção, simulando um aluno sem risco ou já notificado
        doThrow(new IllegalArgumentException("O aluno não está em Risco Alto no momento."))
                .when(alertaRiscoService).notificarCoordenacao(1);

        mockMvc.perform(post("/alertas-risco/{matriculaId}/notificar", 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O aluno não está em Risco Alto no momento."));
    }

    @Test
    @DisplayName("GET /alertas-risco - Deve retornar 200 OK e a lista de alertas para a coordenação")
    void deveListarAlertasComSucesso() throws Exception {
        // Arrange: Criamos um mock do DTO com os seus dados
        AlertaResponseDTO alertaMock = new AlertaResponseDTO(
                1, "Enio Jr", "2026001", "Programação Orientada a Objetos", "ALTO",
                BigDecimal.valueOf(4.5), 15, "Muitas faltas",
                StatusAlerta.PENDENTE, "Aguardando contato", LocalDateTime.now()
        );

        when(alertaRiscoService.listarTodosAlertas()).thenReturn(List.of(alertaMock));

        // Act & Assert
        mockMvc.perform(get("/alertas-risco")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].alunoNome").value("Enio Jr"))
                .andExpect(jsonPath("$[0].disciplina").value("Programação Orientada a Objetos"))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"));

        verify(alertaRiscoService, times(1)).listarTodosAlertas();
    }

    @Test
    @DisplayName("PUT /alertas-risco/{id} - Deve atualizar o alerta e retornar 204 No Content")
    void deveAtualizarAlertaComSucesso() throws Exception {
        // Arrange
        AtualizarAlertaDTO dto = new AtualizarAlertaDTO(StatusAlerta.EM_ACOMPANHAMENTO, "Reunião marcada com os responsáveis.");

        // Act & Assert
        mockMvc.perform(put("/alertas-risco/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(alertaRiscoService, times(1)).atualizarAlerta(eq(1), any(AtualizarAlertaDTO.class));
    }

    @Test
    @DisplayName("PUT /alertas-risco/{id} - Deve estourar erro 404 se a entidade não existir (comportamento simulado via exception do Service)")
    void deveRetornarErroAoAtualizarAlertaInexistente() throws Exception {
        // Arrange: Cria DTO
        AtualizarAlertaDTO dto = new AtualizarAlertaDTO(StatusAlerta.RESOLVIDO, "Finalizado.");
        
        // Simula a exceção que o Service lança
        doThrow(new RecursoNaoEncontradoException("Alerta não encontrado."))
                .when(alertaRiscoService).atualizarAlerta(eq(99), any(AtualizarAlertaDTO.class));

        // Act & Assert (Se houver um GlobalExceptionHandler que trata, ele retornaria 404. 
        // Como o MockMvc isolado propaga o erro empacotado, vamos verificar se a exceção é lançada/retorna erro de servidor interno no escopo simples)
        try {
            mockMvc.perform(put("/alertas-risco/{id}", 99)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound()); // Supondo o Controller Advice configurado
        } catch (Exception e) {
            // Em testes standalone sem Advice, o erro propaga encrustado
            assertTrue(e.getCause() instanceof RecursoNaoEncontradoException);
        }
    }

    @Test
    @DisplayName("GET /alertas-risco/{id}/historico - Deve retornar 200 OK e a linha do tempo do alerta")
    void deveVerHistoricoComSucesso() throws Exception {
        // Arrange
        HistoricoAlertaDTO historicoMock = new HistoricoAlertaDTO(
                100, "PENDENTE", "Alerta inicial gerado", LocalDateTime.now(), "Sistema"
        );

        when(alertaRiscoService.listarHistorico(1)).thenReturn(List.of(historicoMock));

        // Act & Assert
        mockMvc.perform(get("/alertas-risco/{id}/historico", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$[0].responsavelNome").value("Sistema"));

        verify(alertaRiscoService, times(1)).listarHistorico(1);
    }
}