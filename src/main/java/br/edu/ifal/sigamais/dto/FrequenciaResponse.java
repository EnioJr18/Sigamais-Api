package br.edu.ifal.sigamais.dto;

public record FrequenciaResponse(
    Long id,
    Long matriculaId,
    Integer faltas
) {}