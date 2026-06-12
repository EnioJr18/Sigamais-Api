package br.edu.ifal.sigamais.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Nota;
import br.edu.ifal.sigamais.repository.NotaRepository;

@ExtendWith(MockitoExtension.class)
public class NotaServiceTest {
    
    @Mock
    private NotaRepository notaRepository;

    @InjectMocks
    private NotaService notaService;

    private Matricula matriculaExemplo;

    @BeforeEach
    void setUp() {
        matriculaExemplo = new Matricula();
        matriculaExemplo.setId(1);
    }

    @Test
    @DisplayName("Deve aprovar o aluno quanto a média das notas for maior ou igual a 7.0")
    void deveAprovarAlunoComMediaMaiorOuIgualA7() {
        Integer matriculaId = 1;
        List<Nota> notasSimuladas = Arrays.asList(
            new Nota(null, matriculaExemplo, BigDecimal.valueOf(8.0), "AV1"),
            new Nota(null, matriculaExemplo, BigDecimal.valueOf(7.0), "AV2")
        );

        when(notaRepository.findByMatriculaId(matriculaId)).thenReturn(notasSimuladas);

        boolean resultado = notaService.verificarAprovacaoPorMedia(matriculaId);

        assertTrue(resultado);
        verify(notaRepository, times(1)).findByMatriculaId(matriculaId);
    }

    @Test
    @DisplayName("Deve retornar false se o aluno não tiver nenhuma nota lançada")
    void deveRetornarFalsoSeNaoHouverNotas() {
        Integer matriculaId = 1;
        when(notaRepository.findByMatriculaId(matriculaId)).thenReturn(Collections.emptyList());
        boolean resultado = notaService.verificarAprovacaoPorMedia(matriculaId);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar risco ALTO quando a média das notas for menor 5.0")
    void deveRetornarRiscoAltoParaMediaBaixa() {
        Integer matriculaId = 1;
        List<Nota> notasRuins = Arrays.asList(
            new Nota(null, matriculaExemplo, BigDecimal.valueOf(4.0), "AV1"),
            new Nota(null, matriculaExemplo, BigDecimal.valueOf(4.5), "AV2")
        );

        when(notaRepository.findByMatriculaId(matriculaId)).thenReturn(notasRuins);

        String risco = notaService.calcularRiscoPorNota(matriculaId);

        assertEquals("ALTO", risco);
    }

    @Test
    @DisplayName("Deve retornar risco BAIXO quando a média das notas for maior ou igual a 7.0")
    void deveRetornarRiscoBaixoParaMediaAlta() {
        Integer matriculaId = 1;
        List<Nota> notasBoas = Arrays.asList(
            new Nota(null, matriculaExemplo, BigDecimal.valueOf(8.5), "AV1"),
            new Nota(null, matriculaExemplo, BigDecimal.valueOf(9.0), "AV2")
        );

        when(notaRepository.findByMatriculaId(matriculaId)).thenReturn(notasBoas);

        String risco = notaService.calcularRiscoPorNota(matriculaId);

        assertEquals("BAIXO", risco);
    }
}
