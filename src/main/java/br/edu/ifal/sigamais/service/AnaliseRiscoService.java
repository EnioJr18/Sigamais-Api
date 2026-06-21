package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.AlertaRiscoDTO;
import br.edu.ifal.sigamais.model.Frequencia;
import br.edu.ifal.sigamais.model.Nota;
import br.edu.ifal.sigamais.repository.FrequenciaRepository;
import br.edu.ifal.sigamais.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnaliseRiscoService {

    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;

    public AlertaRiscoDTO analisarRiscoMatricula(Integer matriculaId) {
        // 1. Calcula a Média
        List<Nota> notas = notaRepository.findByMatriculaId(matriculaId);
        BigDecimal media = BigDecimal.ZERO;

        if (!notas.isEmpty()) {
            BigDecimal soma = BigDecimal.ZERO;
            for (Nota n : notas) {
                soma = soma.add(n.getValor());
            }
            media = soma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
        }

        // 2. Soma as Faltas
        List<Frequencia> frequencias = frequenciaRepository.findByMatriculaId(matriculaId);
        int totalFaltas = 0;
        for (Frequencia f : frequencias) {
            totalFaltas += f.getFaltas();
        }

        // 3. Aplica as Regras do David e gera os Motivos
        String risco;
        List<String> motivos = new ArrayList<>();

        // Regra ALTO: media < 5 OU faltas >= 12
        if (media.compareTo(new BigDecimal("5.0")) < 0 || totalFaltas >= 12) {
            risco = "ALTO";
            if (media.compareTo(new BigDecimal("5.0")) < 0) {
                motivos.add("Média acadêmica crítica (abaixo de 5.0)");
            }
            if (totalFaltas >= 12) {
                motivos.add("Alto índice de faltas (" + totalFaltas + " faltas)");
            }
        }
        // Regra MEDIO: media < 7 OU faltas >= 6
        else if (media.compareTo(new BigDecimal("7.0")) < 0 || totalFaltas >= 6) {
            risco = "MEDIO";
            if (media.compareTo(new BigDecimal("7.0")) < 0) {
                motivos.add("Média requer atenção (abaixo de 7.0)");
            }
            if (totalFaltas >= 6) {
                motivos.add("Acúmulo de faltas (" + totalFaltas + " faltas)");
            }
        }
        // Regra BAIXO: media >= 7 E faltas < 6
        else {
            risco = "BAIXO";
            motivos.add("Desempenho e frequência regulares");
        }

        // 4. Devolve o DTO prontinho para o Front-end!
        return new AlertaRiscoDTO(risco, media, totalFaltas, motivos);
    }
}