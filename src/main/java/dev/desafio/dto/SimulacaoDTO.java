package dev.desafio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SimulacaoDTO {

    // Envelope de ENTRADA (Request)
    public static class Request {
        public Long clienteId;
        public BigDecimal valor;
        public Integer prazoMeses;
        public String tipoProduto; // Ex: "CDB", "Fundo", "LCI"
    }

    // Envelope de SAÍDA (Response)
    public static class Response {
        public ProdutoInfo produtoValidado;
        public ResultadoSimulacao resultadoSimulacao;
        public LocalDateTime dataSimulacao;

        public Response(ProdutoInfo produto, ResultadoSimulacao resultado) {
            this.produtoValidado = produto;
            this.resultadoSimulacao = resultado;
            this.dataSimulacao = LocalDateTime.now();
        }
    }

    public static class ProdutoInfo {
        public Long id;
        public String nome;
        public String tipo;
        public BigDecimal rentabilidade;
        public String risco;
    }

    public static class ResultadoSimulacao {
        public BigDecimal valorFinal;
        public BigDecimal rentabilidadeEfetiva; // Rentabilidade total no período
        public Integer prazoMeses;
    }
}