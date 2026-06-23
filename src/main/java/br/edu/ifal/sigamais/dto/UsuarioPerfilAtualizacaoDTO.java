package br.edu.ifal.sigamais.dto;

public record UsuarioPerfilAtualizacaoDTO(
        String nome,
        String telefone,
        String endereco,
        String fotoPerfilUrl
) {}