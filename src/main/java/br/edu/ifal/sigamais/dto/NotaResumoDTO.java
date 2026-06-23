package br.edu.ifal.sigamais.dto;

import java.math.BigDecimal;
import java.util.List;

public record NotaResumoDTO(
        Integer matriculaId,
        String alunoNome,
        String alunoMatricula,
        String disciplinaNome,
        String professorNome,
        String semestre,
        BigDecimal media,
        Integer quantidadeNotas,
        String situacao,
        List<NotaDetalheDTO> notas
) {}