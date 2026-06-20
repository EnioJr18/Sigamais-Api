package br.edu.ifal.sigamais.dto;

public record TurmaRequestDTO(
        Integer professorId,
        Integer disciplinaId,
        String semestre,
        Integer ano,
        Integer vagas // O campo que estava faltando!
) {}