package br.edu.ifal.sigamais.dto;

import br.edu.ifal.sigamais.model.enums.StatusAlerta;

public record AtualizarAlertaDTO(
        StatusAlerta status,
        String observacao
) {}