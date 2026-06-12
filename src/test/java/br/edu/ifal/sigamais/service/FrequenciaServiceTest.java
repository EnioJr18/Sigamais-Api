package br.edu.ifal.sigamais.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
