package dev.desafio.service;

import dev.desafio.entity.HistoricoInvestimento;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class PerfilRiscoService {

    public record PerfilCalculado(String perfil, int pontuacao, String descricao) {}

    public PerfilCalculado calcularPerfil(Long clienteId) {
        List<HistoricoInvestimento> historico = HistoricoInvestimento.list("clienteId", clienteId);

        if (historico.isEmpty()) {
            return new PerfilCalculado("Conservador", 0, "Sem histórico: Perfil padrão de segurança.");
        }

        // Algoritmo de Pontuação baseado no desafio: Volume, Frequência e Tipo
        int pontos = 0;
        BigDecimal volumeTotal = BigDecimal.ZERO;

        for (HistoricoInvestimento inv : historico) {
            volumeTotal = volumeTotal.add(inv.valor);

            // Pontos por tipo de investimento
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
        int pontosVolume = volumeTotal.divideToIntegralValue(new BigDecimal("10000")).intValue() * 5;
        pontos += pontosVolume;

        // Pontos por Frequência (Ex: +2 pontos por cada investimento realizado)
        pontos += (historico.size() * 2);

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