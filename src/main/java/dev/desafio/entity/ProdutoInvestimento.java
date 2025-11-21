package dev.desafio.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class ProdutoInvestimento extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String nome;

    @Column(nullable = false)
    public String tipo;

    @Column(nullable = false)
    @JsonProperty("rentabilidade")
    public BigDecimal rentabilidadeAnual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public NivelRisco risco; // Usa o Enum que já criamos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
