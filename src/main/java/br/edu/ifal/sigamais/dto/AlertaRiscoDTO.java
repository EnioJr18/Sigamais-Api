package br.edu.ifal.sigamais.dto;

import java.math.BigDecimal;
import java.util.List;

public record AlertaRiscoDTO(
        String risco,
        BigDecimal media,
        Integer faltas,
        List<String> motivos
) {}