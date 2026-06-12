package br.edu.ifal.sigamais.dto;

public record FrequenciaResponseDTO(
    Long id,
    Integer matriculaId,
    Integer faltas
) {}