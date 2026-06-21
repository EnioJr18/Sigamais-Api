package br.edu.ifal.sigamais.dto;

public record ProfessorRequestDTO(
        String nome,
        String email,
        String cpf,
        String senha,
        String titulacao
) {}