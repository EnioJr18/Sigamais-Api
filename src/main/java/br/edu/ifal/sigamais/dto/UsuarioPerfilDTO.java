package br.edu.ifal.sigamais.dto;

public record UsuarioPerfilDTO(
        Integer id,
        String nome,
        String cpf,
        String email,
        String perfil,
        String telefone,
        String endereco,
        String fotoPerfilUrl
) {}