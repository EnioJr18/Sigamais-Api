package br.edu.ifal.sigamais.dto;

public record AlunoResponseDTO(
        Integer id,
        String nome,
        String matricula,
        String curso,
        String status
) {
}