package dev.desafio.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TelemetriaDTO {

    public List<ServicoMetrica> servicos;
    public Periodo periodo;

    public TelemetriaDTO(List<ServicoMetrica> servicos, LocalDateTime inicio, LocalDateTime fim) {
        this.servicos = servicos;
        this.periodo = new Periodo(inicio, fim);
    }

    public static class ServicoMetrica {
        public String nome;
        public long quantidadeChamadas;
        public double mediaTempoRespostaMs;

        public ServicoMetrica(String nome, long quantidadeChamadas, double mediaTempoRespostaMs) {
            this.nome = nome;
            this.quantidadeChamadas = quantidadeChamadas;
            this.mediaTempoRespostaMs = mediaTempoRespostaMs;
        }
    }

    public static class Periodo {
        public LocalDateTime inicio;
        public LocalDateTime fim;

        public Periodo(LocalDateTime inicio, LocalDateTime fim) {
            this.inicio = inicio;
            this.fim = fim;
        }
    }
}