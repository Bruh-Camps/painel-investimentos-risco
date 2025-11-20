package dev.desafio.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

@ApplicationScoped
public class TelemetriaService {

    // Guarda as métricas por nome do serviço
    private final Map<String, DadosMetrica> estatisticas = new ConcurrentHashMap<>();

    public void registrar(String servico, long tempoExecucaoMs) {
        estatisticas.computeIfAbsent(servico, k -> new DadosMetrica())
                .atualizar(tempoExecucaoMs);
    }

    public DadosMetrica ler(String servico) {
        return estatisticas.getOrDefault(servico, new DadosMetrica());
    }

    // Classe interna para thread-safety (evitar problemas de concorrência)
    public static class DadosMetrica {
        private final AtomicLong quantidade = new AtomicLong(0);
        private final AtomicLong somaTempos = new AtomicLong(0);

        public void atualizar(long tempoMs) {
            quantidade.incrementAndGet();
            somaTempos.addAndGet(tempoMs);
        }

        public long getQuantidade() {
            return quantidade.get();
        }

        public double getMedia() {
            long qtd = quantidade.get();
            if (qtd == 0) return 0.0;
            return (double) somaTempos.get() / qtd;
        }
    }
}