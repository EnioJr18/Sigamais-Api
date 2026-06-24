package br.edu.ifal.sigamais.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifal.sigamais.dto.NotaRequestDTO;
import br.edu.ifal.sigamais.dto.NotaResponseDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Nota;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import br.edu.ifal.sigamais.repository.NotaRepository;

@ExtendWith(MockitoExtension.class)
public class NotaServiceTest {
    
    @Mock
    private NotaRepository notaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @InjectMocks
    private NotaService notaService;

    private Matricula matriculaExemplo;

    @BeforeEach
    void setUp() {
        matriculaExemplo = new Matricula();
        matriculaExemplo.setId(1);
    }

    @Test
    @DisplayName("Deve cadastrar uma nota com sucesso e retornar DTO")
    void deveCadastrarNotaComSucesso() {
        NotaRequestDTO requestDTO = new NotaRequestDTO(1, new BigDecimal("8.5"), "AV1");

        when(matriculaRepository.findById(1)).thenReturn(Optional.of(matriculaExemplo));

        Nota notaSalva = new Nota(1, matriculaExemplo, new BigDecimal("8.5"), "AV1");
        when(notaRepository.save(any(Nota.class))).thenReturn(notaSalva);

        NotaResponseDTO response = notaService.cadastrarNota(requestDTO);

        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals(1, response.matriculaId());
        assertEquals(new BigDecimal("8.5"), response.valor());
        assertEquals("AV1", response.tipo());
        verify(notaRepository).save(any(Nota.class));
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao cadastrar nota com matrícula inexistente")
    void deveLancarExcecaoAoCadastrarNotaComMatriculaInexistente() {
        NotaRequestDTO requestDTO = new NotaRequestDTO(99, new BigDecimal("7.0"), "AV1");

        when(matriculaRepository.findById(99)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> {
            notaService.cadastrarNota(requestDTO);
        });

        assertEquals("Matrícula não encontrada com o ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar as notas de uma matrícula com sucesso")
    void deveListarNotasPorMatricula() {
        List<Nota> notasSimuladas = Arrays.asList(
            new Nota(1, matriculaExemplo, new BigDecimal("8.0"), "AV1"),
            new Nota(2, matriculaExemplo, new BigDecimal("7.0"), "AV2")
        );

        when(notaRepository.findByMatriculaId(1)).thenReturn(notasSimuladas);

        List<NotaResponseDTO> response = notaService.listarNotasPorMatricula(1);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(new BigDecimal("8.0"), response.get(0).valor());
        assertEquals(new BigDecimal("7.0"), response.get(1).valor());
    }

    @Test
    @DisplayName("Deve aprovar o aluno quando a média das notas for maior ou igual a 7.0")
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
    @DisplayName("Deve retornar risco MEDIO quando a média for >= 5.0 e < 7.0")
    void deveRetornarRiscoMedioParaMediaRegular() {
        Integer matriculaId = 1;
        List<Nota> notasRegulares = Arrays.asList(
            new Nota(null, matriculaExemplo, BigDecimal.valueOf(6.0), "AV1"),
            new Nota(null, matriculaExemplo, BigDecimal.valueOf(6.5), "AV2")
        );

        when(notaRepository.findByMatriculaId(matriculaId)).thenReturn(notasRegulares);

        String risco = notaService.calcularRiscoPorNota(matriculaId);

        assertEquals("MEDIO", risco);
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

    @Test
    @DisplayName("Deve retornar risco MEDIO se o aluno não tiver nenhuma nota lançada (default)")
    void deveRetornarRiscoMedioSeNaoHouverNotas() {
        Integer matriculaId = 1;
        when(notaRepository.findByMatriculaId(matriculaId)).thenReturn(Collections.emptyList());

        String risco = notaService.calcularRiscoPorNota(matriculaId);

        assertEquals("MEDIO", risco);
    }
}
