package br.edu.ifal.sigamais.dto;

import br.edu.ifal.sigamais.model.enums.StatusAlerta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlertaResponseDTO(
        Integer id,
        String alunoNome,
        String alunoMatricula,
        String disciplina,
        String risco,
        BigDecimal media,
        Integer faltas,
        String motivos,
        StatusAlerta status,
        String observacao,
        LocalDateTime criadoEm
) {}