package br.edu.ifal.sigamais.dto;

import java.math.BigDecimal;

public record NotaResponseDTO(
    Integer id,
    Integer matriculaId,
    BigDecimal valor,
    String tipo
) {}