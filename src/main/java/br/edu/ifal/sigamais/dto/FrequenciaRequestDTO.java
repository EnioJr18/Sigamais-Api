package br.edu.ifal.sigamais.dto;

public record FrequenciaRequestDTO(
    Long matriculaId,
    Integer faltas
) {}