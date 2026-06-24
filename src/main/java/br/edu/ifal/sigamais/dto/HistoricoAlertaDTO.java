package br.edu.ifal.sigamais.dto;

import java.time.LocalDateTime;

public record HistoricoAlertaDTO(
        Integer id,
        String status,
        String observacao,
        LocalDateTime criadoEm,
        String responsavelNome
) {}