package br.edu.ifal.sigamais.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifal.sigamais.dto.FrequenciaRequestDTO;
import br.edu.ifal.sigamais.dto.FrequenciaResponseDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Disciplina;
import br.edu.ifal.sigamais.model.Frequencia;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.repository.FrequenciaRepository;
import br.edu.ifal.sigamais.repository.MatriculaRepository;

@ExtendWith(MockitoExtension.class)
public class FrequenciaServiceTest {
    
    @Mock
    private FrequenciaRepository frequenciaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @InjectMocks
    private FrequenciaService frequenciaService;

    private Matricula matriculaExemplo;
    
    @BeforeEach
    void setUp() {
        Disciplina disciplina = new Disciplina();
        disciplina.setCargaHoraria(60);

        Turma turma = new Turma();
        turma.setDisciplina(disciplina);

        matriculaExemplo = new Matricula();
        matriculaExemplo.setId(1);
        matriculaExemplo.setTurma(turma);
    }

    @Test
    @DisplayName("Deve registrar frequência com sucesso")
    void deveRegistrarFrequenciaComSucesso() {
        FrequenciaRequestDTO requestDTO = new FrequenciaRequestDTO(1, 4);
        
        Frequencia frequenciaSalva = new Frequencia(1L, matriculaExemplo, 4);

        when(matriculaRepository.findById(1)).thenReturn(Optional.of(matriculaExemplo));
        when(frequenciaRepository.save(any(Frequencia.class))).thenReturn(frequenciaSalva);

        FrequenciaResponseDTO response = frequenciaService.registrarFrequencia(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(1, response.matriculaId());
        assertEquals(4, response.faltas());
        verify(frequenciaRepository).save(any(Frequencia.class));
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao registrar frequência para matrícula inexistente")
    void deveLancarExcecaoAoRegistrarFrequenciaComMatriculaInexistente() {
        FrequenciaRequestDTO requestDTO = new FrequenciaRequestDTO(99, 2);

        when(matriculaRepository.findById(99)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            frequenciaService.registrarFrequencia(requestDTO);
        });

        assertEquals("Matrícula não encontrada com o ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar frequência por matrícula com sucesso")
    void deveListarFrequenciaPorMatriculaComSucesso() {
        List<Frequencia> frequencias = Arrays.asList(
            new Frequencia(1L, matriculaExemplo, 2),
            new Frequencia(2L, matriculaExemplo, 4)
        );

        when(frequenciaRepository.findByMatriculaId(1)).thenReturn(frequencias);

        List<FrequenciaResponseDTO> response = frequenciaService.listarFrequenciaPorMatricula(1);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(2, response.get(0).faltas());
        assertEquals(4, response.get(1).faltas());
    }

    @Test
    @DisplayName("Deve reprovar o aluno quando o total de faltas estourar 25% da carga horária")
    void deveReprovarAlunoQuandoEstourarLimiteDeFaltas() {
        Integer matriculaId = 1;
        List<Frequencia> faltasSimuladas = Arrays.asList(
            new Frequencia(null, matriculaExemplo, 10),
            new Frequencia(null, matriculaExemplo, 6)
        );

        when(frequenciaRepository.findByMatriculaId(matriculaId)).thenReturn(faltasSimuladas);
        when(matriculaRepository.findById(matriculaId)).thenReturn(Optional.of(matriculaExemplo));

        boolean resultado = frequenciaService.verificarReprovacaoPorFalta(matriculaId);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Não deve reprovar o aluno quando o total de faltas for menor ou igual a 25% da carga horária")
    void naoDeveReprovarAlunoQuandoFaltasEstiveremDentroDoLimite() {
        Integer matriculaId = 1;
        List<Frequencia> faltasSimuladas = Arrays.asList(
            new Frequencia(null, matriculaExemplo, 5),
            new Frequencia(null, matriculaExemplo, 7)
        );

        when(frequenciaRepository.findByMatriculaId(matriculaId)).thenReturn(faltasSimuladas);
        when(matriculaRepository.findById(matriculaId)).thenReturn(Optional.of(matriculaExemplo));

        boolean resultado = frequenciaService.verificarReprovacaoPorFalta(matriculaId);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar risco BAIXO quando o aluno tiver poucas faltas")
    void deveRetornarRiscoBaixoParaPoucasFaltas() {
        Integer matriculaId = 1;
        List<Frequencia> poucasFaltas = List.of(new Frequencia(null, matriculaExemplo, 4));

        when(frequenciaRepository.findByMatriculaId(matriculaId)).thenReturn(poucasFaltas);
        when(matriculaRepository.findById(matriculaId)).thenReturn(Optional.of(matriculaExemplo));

        String risco = frequenciaService.calcularRiscoPorFalta(matriculaId);

        assertEquals("BAIXO", risco);
    }

    @Test
    @DisplayName("Deve retornar risco MEDIO quando proporção gasta for maior ou igual a 50% e menor ou igual a 80%")
    void deveRetornarRiscoMedioParaMuitasFaltas() {
        Integer matriculaId = 1;
        // Carga horaria 60. Limite = 15. Proporção = faltas / 15.
        // Faltas = 10 -> 10/15 = 0.66 (> 0.5)
        List<Frequencia> faltas = List.of(new Frequencia(null, matriculaExemplo, 10));

        when(frequenciaRepository.findByMatriculaId(matriculaId)).thenReturn(faltas);
        when(matriculaRepository.findById(matriculaId)).thenReturn(Optional.of(matriculaExemplo));

        String risco = frequenciaService.calcularRiscoPorFalta(matriculaId);

        assertEquals("MEDIO", risco);
    }

    @Test
    @DisplayName("Deve retornar risco ALTO quando proporção gasta for maior que 80%")
    void deveRetornarRiscoAltoParaQuaseEstouro() {
        Integer matriculaId = 1;
        // Limite = 15. Faltas = 13 -> 13/15 = 0.86 (> 0.8)
        List<Frequencia> faltas = List.of(new Frequencia(null, matriculaExemplo, 13));

        when(frequenciaRepository.findByMatriculaId(matriculaId)).thenReturn(faltas);
        when(matriculaRepository.findById(matriculaId)).thenReturn(Optional.of(matriculaExemplo));

        String risco = frequenciaService.calcularRiscoPorFalta(matriculaId);

        assertEquals("ALTO", risco);
    }

    @Test
    @DisplayName("Deve propagar RuntimeException ao calcular risco de matricula inexistente")
    void deveLancarExcecaoAoCalcularRiscoMatriculaInexistente() {
        Integer matriculaId = 99;
        when(frequenciaRepository.findByMatriculaId(matriculaId)).thenReturn(List.of());
        when(matriculaRepository.findById(matriculaId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            frequenciaService.calcularRiscoPorFalta(matriculaId);
        });

        assertEquals("Matrícula não encontrada!", exception.getMessage());
    }
}
