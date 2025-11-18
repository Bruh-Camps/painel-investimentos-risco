package dev.desafio.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class HistoricoInvestimento extends PanacheEntity {

    @Column(nullable = false)
    public Long clienteId; // ID do cliente (pode ser o ID do usuário)

    @Column(nullable = false)
    public String tipo; // Tipo do investimento (ex: CDB, Fundo)

    @Column(nullable = false)
    public BigDecimal valor;

    @Column(nullable = false)
    public BigDecimal rentabilidade;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String data;
}
