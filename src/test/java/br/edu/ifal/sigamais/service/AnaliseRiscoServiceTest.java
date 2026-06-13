package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.strategy.RiscoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class AnaliseRiscoServiceTest {

    @InjectMocks
    private AnaliseRiscoService analiseRiscoService;

    @Mock
    private RiscoStrategy estrategiaNotaMock;

    @Mock
    private RiscoStrategy estrategiaFaltaMock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                analiseRiscoService,
                "estrategiasDeRisco",
                Arrays.asList(estrategiaNotaMock, estrategiaFaltaMock)
        );
    }

    @Test
    @DisplayName("Deve retornar risco BAIXO quando todos os cenários estiverem tranquilos")
    void deveRetornarRiscoBaixo() {
        Mockito.when(estrategiaNotaMock.avaliarRisco(anyInt())).thenReturn("BAIXO");
        Mockito.when(estrategiaFaltaMock.avaliarRisco(anyInt())).thenReturn("BAIXO");

        String resultado = analiseRiscoService.analisarRiscoGlobal(1);

        assertEquals("BAIXO", resultado);
    }

    @Test
    @DisplayName("Deve retornar risco MEDIO se pelo menos um alerta for ativado e nenhum for crítico")
    void deveRetornarRiscoMedio() {

        Mockito.when(estrategiaNotaMock.avaliarRisco(anyInt())).thenReturn("BAIXO");

        Mockito.when(estrategiaFaltaMock.avaliarRisco(anyInt())).thenReturn("MEDIO");

        String resultado = analiseRiscoService.analisarRiscoGlobal(1);

        assertEquals("MEDIO", resultado);
    }

    @Test
    @DisplayName("Deve retornar risco ALTO imediatamente se detectar um cenário crítico")
    void deveRetornarRiscoAlto() {

        Mockito.when(estrategiaNotaMock.avaliarRisco(anyInt())).thenReturn("ALTO");

        Mockito.lenient().when(estrategiaFaltaMock.avaliarRisco(anyInt())).thenReturn("BAIXO");

        String resultado = analiseRiscoService.analisarRiscoGlobal(1);

        assertEquals("ALTO", resultado);
    }
}