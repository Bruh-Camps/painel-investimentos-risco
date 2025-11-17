package dev.desafio.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

@Entity
public class ProdutoInvestimento extends PanacheEntity {

    @Column(nullable = false)
    public String nome;

    @Column(nullable = false)
    public String tipo;

    @Column(nullable = false)
    public BigDecimal rentabilidadeAnual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public NivelRisco risco; // Usa o Enum que já criamos
}
