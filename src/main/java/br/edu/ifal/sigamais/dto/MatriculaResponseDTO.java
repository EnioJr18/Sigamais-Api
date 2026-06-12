package br.edu.ifal.sigamais.dto;

public record MatriculaResponseDTO(
        Integer id,
        String matriculaAluno,
        String nomeDisciplina,
        String status
) {
}
