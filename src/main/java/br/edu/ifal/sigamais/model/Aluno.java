package br.edu.ifal.sigamais.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aluno")
@Data
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relacionamento 1 para 1 com a tabela de Usuários
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Column(unique = true)
    private String matricula;

    private String curso;

    @Column(name = "renda_familiar")
    private BigDecimal rendaFamiliar;

    @Column(name = "ano_ingresso")
    private Integer anoIngresso;

    private String status; // Ex: "ATIVO", "TRANCADO", "EVADIDO"

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}