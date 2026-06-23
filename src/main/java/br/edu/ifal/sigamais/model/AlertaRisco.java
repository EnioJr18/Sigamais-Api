package br.edu.ifal.sigamais.model;

import br.edu.ifal.sigamais.model.enums.StatusAlerta;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class AlertaRisco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "matricula_id", unique = true)
    private Matricula matricula;

    private String risco;
    private BigDecimal media;
    private Integer faltas;

    @Column(columnDefinition = "TEXT")
    private String motivos;

    @Enumerated(EnumType.STRING)
    private StatusAlerta status = StatusAlerta.PENDENTE;

    private boolean emailEnviado = false;

    private LocalDateTime criadoEm = LocalDateTime.now();
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String observacao;
}