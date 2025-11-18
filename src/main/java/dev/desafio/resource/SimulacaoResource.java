package dev.desafio.resource;

import dev.desafio.dto.SimulacaoDTO;
import dev.desafio.entity.ProdutoInvestimento;
import dev.desafio.entity.Simulacao;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Path("/simular-investimento")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @POST
    @Transactional
    public Response simular(SimulacaoDTO.Request request) {
        // 1. Validar e buscar produto compatível
        // Busca o primeiro produto que corresponda ao tipo solicitado (Ex: "CDB")
        ProdutoInvestimento produto = ProdutoInvestimento.find("tipo", request.tipoProduto).firstResult();

        if (produto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nenhum produto encontrado para o tipo: " + request.tipoProduto)
                    .build();
        }

        // 2. Realizar Cálculos (Juros Compostos)
        // Fórmula: M = C * (1 + i)^t
        // Onde i deve ser transformado de anual para mensal (simplificação ou cálculo exato)

        BigDecimal taxaAnual = produto.rentabilidadeAnual.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);

        // Cálculo da taxa mensal equivalente: (1 + anual)^(1/12) - 1
        double taxaMensalDouble = Math.pow(1 + taxaAnual.doubleValue(), 1.0 / 12.0) - 1;
        BigDecimal taxaMensal = BigDecimal.valueOf(taxaMensalDouble);

        // Fator de crescimento total: (1 + mensal)^meses
        BigDecimal fatorCrescimento = taxaMensal.add(BigDecimal.ONE).pow(request.prazoMeses);

        BigDecimal valorFinal = request.valor.multiply(fatorCrescimento).setScale(2, RoundingMode.HALF_UP);

        // 3. Preparar os dados de retorno
        SimulacaoDTO.ProdutoInfo produtoInfo = new SimulacaoDTO.ProdutoInfo();
        produtoInfo.id = produto.id;
        produtoInfo.nome = produto.nome;
        produtoInfo.tipo = produto.tipo;
        produtoInfo.rentabilidade = produto.rentabilidadeAnual; // Retorna a anual para referência
        produtoInfo.risco = produto.risco.name();

        SimulacaoDTO.ResultadoSimulacao resultado = new SimulacaoDTO.ResultadoSimulacao();
        resultado.valorFinal = valorFinal;
        resultado.prazoMeses = request.prazoMeses;
        // Rentabilidade efetiva no período (Valor Final - Inicial) / Inicial
        resultado.rentabilidadeEfetiva = valorFinal.subtract(request.valor)
                .divide(request.valor, 4, RoundingMode.HALF_UP);

        // 4. Persistir a simulação no banco (Requisito do desafio)
        Simulacao novaSimulacao = new Simulacao();
        novaSimulacao.clienteId = request.clienteId;
        novaSimulacao.valorInvestido = request.valor;
        novaSimulacao.valorFinal = valorFinal;
        novaSimulacao.prazoMeses = request.prazoMeses;
        novaSimulacao.produtoNome = produto.nome;
        novaSimulacao.produtoTipo = produto.tipo;
        novaSimulacao.dataSimulacao = LocalDateTime.now();
        novaSimulacao.persist();

        // 5. Retornar envelope JSON
        return Response.ok(new SimulacaoDTO.Response(produtoInfo, resultado)).build();
    }
}