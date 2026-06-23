package br.edu.ifal.sigamais.dto;

import java.math.BigDecimal;

public record NotaDetalheDTO(
        Integer id,
        String tipo, // Ex: "PROVA", "TRABALHO".
        BigDecimal valor
) {}