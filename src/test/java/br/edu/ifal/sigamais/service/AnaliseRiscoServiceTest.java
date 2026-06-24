package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.AlertaRiscoDTO;
import br.edu.ifal.sigamais.model.Frequencia;
import br.edu.ifal.sigamais.model.Nota;
import br.edu.ifal.sigamais.repository.FrequenciaRepository;
import br.edu.ifal.sigamais.repository.NotaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AnaliseRiscoServiceTest {

    @InjectMocks
    private AnaliseRiscoService analiseRiscoService;

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private FrequenciaRepository frequenciaRepository;

    @Test
    @DisplayName("Deve retornar risco BAIXO quando notas e faltas estão no limite ideal")
    void deveRetornarRiscoBaixo() {
        Nota nota1 = new Nota(); nota1.setValor(new BigDecimal("7.0"));
        Nota nota2 = new Nota(); nota2.setValor(new BigDecimal("8.0"));
        
        Frequencia freq = new Frequencia(); freq.setFaltas(5);

        Mockito.when(notaRepository.findByMatriculaId(1)).thenReturn(List.of(nota1, nota2));
        Mockito.when(frequenciaRepository.findByMatriculaId(1)).thenReturn(List.of(freq));

        AlertaRiscoDTO resultado = analiseRiscoService.analisarRiscoMatricula(1);

        assertEquals("BAIXO", resultado.risco());
        assertEquals(new BigDecimal("7.50"), resultado.media());
        assertEquals(5, resultado.faltas());
        assertTrue(resultado.motivos().contains("Desempenho e frequência regulares"));
    }

    @Test
    @DisplayName("Deve retornar risco MEDIO se a média for abaixo de 7.0 e faltas < 6")
    void deveRetornarRiscoMedioPorMedia() {
        Nota nota1 = new Nota(); nota1.setValor(new BigDecimal("6.0"));
        
        Frequencia freq = new Frequencia(); freq.setFaltas(2);

        Mockito.when(notaRepository.findByMatriculaId(2)).thenReturn(List.of(nota1));
        Mockito.when(frequenciaRepository.findByMatriculaId(2)).thenReturn(List.of(freq));

        AlertaRiscoDTO resultado = analiseRiscoService.analisarRiscoMatricula(2);

        assertEquals("MEDIO", resultado.risco());
        assertEquals(new BigDecimal("6.00"), resultado.media());
        assertEquals(2, resultado.faltas());
        assertTrue(resultado.motivos().contains("Média requer atenção (abaixo de 7.0)"));
    }

    @Test
    @DisplayName("Deve retornar risco MEDIO se a média for >= 7.0 mas faltas forem >= 6 e < 12")
    void deveRetornarRiscoMedioPorFaltas() {
        Nota nota1 = new Nota(); nota1.setValor(new BigDecimal("8.0"));
        
        Frequencia freq = new Frequencia(); freq.setFaltas(8);

        Mockito.when(notaRepository.findByMatriculaId(3)).thenReturn(List.of(nota1));
        Mockito.when(frequenciaRepository.findByMatriculaId(3)).thenReturn(List.of(freq));

        AlertaRiscoDTO resultado = analiseRiscoService.analisarRiscoMatricula(3);

        assertEquals("MEDIO", resultado.risco());
        assertEquals(new BigDecimal("8.00"), resultado.media());
        assertEquals(8, resultado.faltas());
        assertTrue(resultado.motivos().contains("Acúmulo de faltas (8 faltas)"));
    }

    @Test
    @DisplayName("Deve retornar risco ALTO se a média for < 5.0")
    void deveRetornarRiscoAltoPorMedia() {
        Nota nota1 = new Nota(); nota1.setValor(new BigDecimal("4.5"));
        
        Frequencia freq = new Frequencia(); freq.setFaltas(2);

        Mockito.when(notaRepository.findByMatriculaId(4)).thenReturn(List.of(nota1));
        Mockito.when(frequenciaRepository.findByMatriculaId(4)).thenReturn(List.of(freq));

        AlertaRiscoDTO resultado = analiseRiscoService.analisarRiscoMatricula(4);

        assertEquals("ALTO", resultado.risco());
        assertEquals(new BigDecimal("4.50"), resultado.media());
        assertEquals(2, resultado.faltas());
        assertTrue(resultado.motivos().contains("Média acadêmica crítica (abaixo de 5.0)"));
    }

    @Test
    @DisplayName("Deve retornar risco ALTO se as faltas forem >= 12")
    void deveRetornarRiscoAltoPorFaltas() {
        Nota nota1 = new Nota(); nota1.setValor(new BigDecimal("8.0"));
        
        Frequencia freq = new Frequencia(); freq.setFaltas(12);

        Mockito.when(notaRepository.findByMatriculaId(5)).thenReturn(List.of(nota1));
        Mockito.when(frequenciaRepository.findByMatriculaId(5)).thenReturn(List.of(freq));

        AlertaRiscoDTO resultado = analiseRiscoService.analisarRiscoMatricula(5);

        assertEquals("ALTO", resultado.risco());
        assertEquals(new BigDecimal("8.00"), resultado.media());
        assertEquals(12, resultado.faltas());
        assertTrue(resultado.motivos().contains("Alto índice de faltas (12 faltas)"));
    }

    @Test
    @DisplayName("Deve retornar risco ALTO e múltiplos motivos se media < 5.0 e faltas >= 12")
    void deveRetornarRiscoAltoMediaEFaltas() {
        Nota nota1 = new Nota(); nota1.setValor(new BigDecimal("4.0"));
        
        Frequencia freq1 = new Frequencia(); freq1.setFaltas(10);
        Frequencia freq2 = new Frequencia(); freq2.setFaltas(3); // 13 faltas no total

        Mockito.when(notaRepository.findByMatriculaId(6)).thenReturn(List.of(nota1));
        Mockito.when(frequenciaRepository.findByMatriculaId(6)).thenReturn(List.of(freq1, freq2));

        AlertaRiscoDTO resultado = analiseRiscoService.analisarRiscoMatricula(6);

        assertEquals("ALTO", resultado.risco());
        assertEquals(13, resultado.faltas());
        assertEquals(2, resultado.motivos().size());
        assertTrue(resultado.motivos().contains("Média acadêmica crítica (abaixo de 5.0)"));
        assertTrue(resultado.motivos().contains("Alto índice de faltas (13 faltas)"));
    }

    @Test
    @DisplayName("Deve lidar com matricula sem notas cadastradas, considerando media ZERO")
    void deveLidarComMatriculaSemNotas() {
        Frequencia freq = new Frequencia(); freq.setFaltas(0);

        Mockito.when(notaRepository.findByMatriculaId(7)).thenReturn(List.of()); // Vazio
        Mockito.when(frequenciaRepository.findByMatriculaId(7)).thenReturn(List.of(freq));

        AlertaRiscoDTO resultado = analiseRiscoService.analisarRiscoMatricula(7);

        assertEquals("ALTO", resultado.risco()); // Media 0.0 é < 5
        assertEquals(BigDecimal.ZERO, resultado.media());
        assertTrue(resultado.motivos().contains("Média acadêmica crítica (abaixo de 5.0)"));
    }

    @Test
    @DisplayName("Deve tratar erro de repositório caso o banco esteja indisponível")
    void deveLancarExcecaoAoFalharPesquisaBanco() {
        Mockito.when(notaRepository.findByMatriculaId(8))
               .thenThrow(new RuntimeException("Conexão recusada"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            analiseRiscoService.analisarRiscoMatricula(8);
        });

        assertEquals("Conexão recusada", exception.getMessage());
    }
}