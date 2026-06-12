package br.edu.ifal.sigamais.dto;

import java.math.BigDecimal;

public record NotaResponse(
    Long id,
    Long matriculaId,
    BigDecimal valor,
    String tipo
) {}