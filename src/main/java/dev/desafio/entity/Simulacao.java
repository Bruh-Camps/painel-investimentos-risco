package dev.desafio.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase; // Mudou de PanacheEntity para Base
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Simulacao extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long clienteId;
    public String produtoNome;
    public String produtoTipo;
    public BigDecimal valorInvestido;
    public BigDecimal valorFinal;
    public Integer prazoMeses;
    public LocalDateTime dataSimulacao;

    public Simulacao() {}
}