package br.edu.ifal.sigamais.dto;

import java.math.BigDecimal;

public record NotaResumoDTO(
        Integer matriculaId,
        String alunoNome,
        String disciplinaNome,
        BigDecimal media,
        Integer quantidadeNotas,
        String situacao // "REGULAR", "RECUPERACAO", "REPROVADO"
) {}