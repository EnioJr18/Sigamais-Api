package br.edu.ifal.sigamais.dto;

public record FrequenciaResumoDTO(
        Integer matriculaId,
        String alunoNome,
        String disciplinaNome,
        Integer totalFaltas,
        Integer quantidadeRegistros
) {}
