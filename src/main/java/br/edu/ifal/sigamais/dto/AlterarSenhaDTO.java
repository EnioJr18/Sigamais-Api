package br.edu.ifal.sigamais.dto;

public record AlterarSenhaDTO(
        String senhaAtual,
        String novaSenha
) {}