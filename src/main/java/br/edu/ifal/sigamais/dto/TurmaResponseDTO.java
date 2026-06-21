package br.edu.ifal.sigamais.dto;

public record TurmaResponseDTO(
        Integer id,
        String semestre,
        Integer ano,
        Integer vagas,
        Integer professorId,
        String professorNome,
        Integer disciplinaId,
        String disciplinaNome
) {}