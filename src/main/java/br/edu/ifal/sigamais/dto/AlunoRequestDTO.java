package br.edu.ifal.sigamais.dto;

import java.math.BigDecimal;

public record AlunoRequestDTO(
        // Dados do Usuário
        String nome,
        String cpf,
        String email,
        String senha,

        // Dados do Aluno
        String matricula,
        String curso,
        BigDecimal rendaFamiliar,
        Integer anoIngresso
) {
}