package br.edu.ifal.sigamais.dto;
import java.math.BigDecimal;

public record NotaRequestDTO (
    Long matriculaId,
    BigDecimal valor,
    String tipo
) {}
