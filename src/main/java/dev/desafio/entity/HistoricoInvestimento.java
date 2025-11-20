package dev.desafio.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class HistoricoInvestimento extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
