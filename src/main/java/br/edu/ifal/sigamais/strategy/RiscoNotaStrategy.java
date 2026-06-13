package br.edu.ifal.sigamais.strategy;

import br.edu.ifal.sigamais.model.Nota;
import br.edu.ifal.sigamais.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class RiscoNotaStrategy implements RiscoStrategy {

    @Autowired
    private NotaRepository notaRepository;

    @Override
    public String avaliarRisco(Integer matriculaId) {
        List<Nota> notas = notaRepository.findByMatriculaId(matriculaId);

        if (notas.isEmpty()) return "BAIXO";

        BigDecimal soma = BigDecimal.ZERO;
        for (Nota nota : notas) {
            soma = soma.add(nota.getValor());
        }

        BigDecimal media = soma.divide(new BigDecimal(notas.size()), 2, RoundingMode.HALF_UP);

        if (media.compareTo(new BigDecimal("5.0")) < 0) return "ALTO";
        if (media.compareTo(new BigDecimal("7.0")) < 0) return "MEDIO";

        return "BAIXO";
    }
}