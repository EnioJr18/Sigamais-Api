package br.edu.ifal.sigamais.dto;

public record FrequenciaResumoDTO(
        Integer matriculaId,
        String alunoNome,
        String alunoMatricula,
        String disciplinaNome,
        String professorNome,
        String semestre,
        Integer totalFaltas,
        Integer quantidadeRegistros
) {}