package dev.desafio.service;

import dev.desafio.entity.HistoricoInvestimento;
import dev.desafio.entity.ProdutoInvestimento;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class PerfilRiscoService {

    // DTO interno para devolver o resultado do cálculo
    public record PerfilCalculado(String perfil, int pontuacao, String descricao) {}

    public PerfilCalculado calcularPerfil(Long clienteId) {
        // 1. Buscar histórico do cliente
        List<HistoricoInvestimento> historico = HistoricoInvestimento.list("clienteId", clienteId);

        if (historico.isEmpty()) {
            return new PerfilCalculado("Conservador", 0, "Sem histórico: Perfil padrão de segurança.");
        }

        // 2. Algoritmo de Pontuação (Scoring)
        // Baseado no desafio: Volume, Frequência e Tipo
        int pontos = 0;
        BigDecimal volumeTotal = BigDecimal.ZERO;

        for (HistoricoInvestimento inv : historico) {
            volumeTotal = volumeTotal.add(inv.valor);

            // Pontos por tipo de investimento (Simplificado)
            // Ações/Fundo agressivo = +20 pontos
            // FII/Fundo moderado = +10 pontos
            // Renda Fixa/CDB = +2 pontos
            String tipo = inv.tipo.toUpperCase();
            if (tipo.contains("AÇÃO") || tipo.contains("ACOES")) {
                pontos += 20;
            } else if (tipo.contains("FUNDO") || tipo.contains("FII")) {
                pontos += 10;
            } else {
                pontos += 2;
            }
        }

        // Pontos por Volume (Ex: +10 pontos a cada 10.000 investidos)
        // Divide por 10000 e pega a parte inteira * 5
        int pontosVolume = volumeTotal.divideToIntegralValue(new BigDecimal("10000")).intValue() * 5;
        pontos += pontosVolume;

        // Pontos por Frequência (Ex: +2 pontos por cada investimento realizado)
        pontos += (historico.size() * 2);

        // 3. Classificação Final
        String perfil;
        String descricao;

        if (pontos >= 50) {
            perfil = "Agressivo";
            descricao = "Busca alta rentabilidade e aceita maiores riscos. Alto volume ou exposição a renda variável.";
        } else if (pontos >= 20) {
            perfil = "Moderado";
            descricao = "Equilíbrio entre liquidez e rentabilidade. Carteira diversificada.";
        } else {
            perfil = "Conservador";
            descricao = "Prioridade por segurança e liquidez. Baixa exposição a riscos.";
        }

        return new PerfilCalculado(perfil, pontos, descricao);
    }
}