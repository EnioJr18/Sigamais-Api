package br.edu.ifal.sigamais.model;

import org.hibernate.annotations.ManyToAny;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "nota")
@Data
public class Nota {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToAny
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 50)
    private String tipo;
}
