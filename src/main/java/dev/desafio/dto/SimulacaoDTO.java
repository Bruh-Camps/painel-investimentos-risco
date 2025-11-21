package dev.desafio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class SimulacaoDTO {

    // Envelope de ENTRADA (Request)
    public static class Request {

        @Schema(hidden = true)
        public Long clienteId;

        @Schema(description = "Valor monetário a investir", examples = {"5000.00"}, required = true)
        public BigDecimal valor;

        @Schema(description = "Tempo de investimento em meses", examples = {"12"}, required = true)
        public Integer prazoMeses;

        @Schema(description = "Tipo de produto financeiro (Deve existir no catálogo)", examples = {"CDB"}, required = true)
        public String tipoProduto; // Ex: "CDB", "Fundo", "LCI"
    }

    // Envelope de SAÍDA (Response)
    public static class Response {
        public Long id;
        public ProdutoInfo produtoValidado;
        public ResultadoSimulacao resultadoSimulacao;
        public LocalDateTime dataSimulacao;

        public Response(ProdutoInfo produto, ResultadoSimulacao resultado) {
            this.id = id;
            this.produtoValidado = produto;
            this.resultadoSimulacao = resultado;
            this.dataSimulacao = LocalDateTime.now();
        }
    }

    public static class AgregadoProdutoDia {
        public String produto;
        public LocalDate data;
        public Long quantidadeSimulacoes;
        public BigDecimal mediaValorFinal;

        public AgregadoProdutoDia(String produto, LocalDate data, Long quantidade, BigDecimal media) {
            this.produto = produto;
            this.data = data;
            this.quantidadeSimulacoes = quantidade;
            this.mediaValorFinal = media;
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