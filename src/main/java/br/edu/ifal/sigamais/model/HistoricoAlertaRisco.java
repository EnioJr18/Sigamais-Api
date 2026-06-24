package br.edu.ifal.sigamais.model;

import br.edu.ifal.sigamais.model.enums.StatusAlerta;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_alerta_risco")
@Data
public class HistoricoAlertaRisco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "alerta_risco_id")
    private AlertaRisco alertaRisco;

    @Enumerated(EnumType.STRING)
    private StatusAlerta status;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    private LocalDateTime criadoEm;

    private String responsavelNome;
}