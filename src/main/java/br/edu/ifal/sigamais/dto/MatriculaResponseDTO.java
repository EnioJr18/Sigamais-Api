package br.edu.ifal.sigamais.dto;

public record MatriculaResponseDTO(
        Integer id,
        Integer alunoId,
        String alunoNome,
        String alunoMatricula,
        Integer turmaId,
        String semestre,
        Integer ano,
        String disciplinaNome,
        String professorNome
) {}