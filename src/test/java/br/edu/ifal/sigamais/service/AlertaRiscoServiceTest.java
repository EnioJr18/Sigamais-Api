package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.AlertaRiscoDTO;
import br.edu.ifal.sigamais.model.AlertaRisco;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.model.Usuario;
import br.edu.ifal.sigamais.repository.AlertaRiscoRepository;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaRiscoServiceTest {

    @InjectMocks
    private AlertaRiscoService alertaRiscoService;

    @Mock
    private AlertaRiscoRepository alertaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AnaliseRiscoService analiseRiscoService;

    @Mock
    private EmailService emailService;

    private Matricula matriculaMock;

    @BeforeEach
    void setUp() {
        // Preparando o terreno com dados falsos para os testes
        Usuario usuario = new Usuario();
        usuario.setNome("Enio Jr");

        Aluno aluno = new Aluno();
        aluno.setUsuario(usuario);

        Disciplina disciplina = new Disciplina();
        disciplina.setNome("Programação Orientada a Objetos");

        Turma turma = new Turma();
        turma.setDisciplina(disciplina);

        matriculaMock = new Matricula();
        matriculaMock.setId(1);
        matriculaMock.setAluno(aluno);
        matriculaMock.setTurma(turma);
    }

    @Test
    @DisplayName("Deve barrar notificação se o risco NÃO for ALTO")
    void naoDeveNotificarSeRiscoBaixoOuMedio() {
        // Arrange (Preparação)
        when(matriculaRepository.findById(1)).thenReturn(Optional.of(matriculaMock));

        AlertaRiscoDTO riscoBaixoDTO = new AlertaRiscoDTO("BAIXO", BigDecimal.TEN, 0, List.of("Tudo certo"));
        when(analiseRiscoService.analisarRiscoMatricula(1)).thenReturn(riscoBaixoDTO);

        // Act & Assert (Ação e Validação)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            alertaRiscoService.notificarCoordenacao(1);
        });

        assertEquals("O aluno não está em Risco Alto no momento.", exception.getMessage());

        // Garante que o e-mail NUNCA foi disparado
        verify(emailService, never()).enviarEmailAlerta(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve barrar notificação se o e-mail já foi enviado antes (Anti-Spam)")
    void naoDeveNotificarSeEmailJaEnviado() {
        // Arrange
        when(matriculaRepository.findById(1)).thenReturn(Optional.of(matriculaMock));

        AlertaRiscoDTO riscoAltoDTO = new AlertaRiscoDTO("ALTO", BigDecimal.ZERO, 20, List.of("Muitas faltas"));
        when(analiseRiscoService.analisarRiscoMatricula(1)).thenReturn(riscoAltoDTO);

        AlertaRisco alertaExistente = new AlertaRisco();
        alertaExistente.setEmailEnviado(true); // Flag de spam ativada!
        when(alertaRepository.findByMatriculaId(1)).thenReturn(Optional.of(alertaExistente));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            alertaRiscoService.notificarCoordenacao(1);
        });

        assertEquals("A coordenação já foi notificada sobre este aluno.", exception.getMessage());
        verify(emailService, never()).enviarEmailAlerta(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve salvar o alerta e enviar o e-mail com sucesso")
    void deveNotificarComSucesso() {
        // Arrange
        when(matriculaRepository.findById(1)).thenReturn(Optional.of(matriculaMock));

        AlertaRiscoDTO riscoAltoDTO = new AlertaRiscoDTO("ALTO", BigDecimal.valueOf(4.5), 15, List.of("Notas baixas"));
        when(analiseRiscoService.analisarRiscoMatricula(1)).thenReturn(riscoAltoDTO);

        when(alertaRepository.findByMatriculaId(1)).thenReturn(Optional.empty()); // Nenhum alerta anterior

        // Act
        alertaRiscoService.notificarCoordenacao(1);

        // Assert
        // Verifica se salvou no banco
        verify(alertaRepository, times(1)).save(any(AlertaRisco.class));
        // Verifica se chamou o serviço de e-mail exatamente 1 vez
        verify(emailService, times(1)).enviarEmailAlerta(anyString(), anyString(), anyString());
    }
}