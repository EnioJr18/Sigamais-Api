package br.edu.ifal.sigamais.strategy;

import br.edu.ifal.sigamais.model.Frequencia;
import br.edu.ifal.sigamais.repository.FrequenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RiscoFaltaStrategy implements RiscoStrategy {

    @Autowired
    private FrequenciaRepository frequenciaRepository;

    @Override
    public String avaliarRisco(Integer matriculaId) {
        List<Frequencia> faltas = frequenciaRepository.findByMatriculaId(matriculaId);

        if (faltas.isEmpty()) return "BAIXO";

        int totalFaltas = faltas.stream().mapToInt(Frequencia::getFaltas).sum();

        if (totalFaltas >= 20) return "ALTO";
        if (totalFaltas >= 14) return "MEDIO";

        return "BAIXO";
    }
}