package dev.desafio.resource;

import dev.desafio.dto.SimulacaoDTO;
import dev.desafio.entity.ProdutoInvestimento;
import dev.desafio.entity.Simulacao;
import dev.desafio.entity.Usuario;
import dev.desafio.service.TelemetriaService;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"user", "admin"})
public class SimulacaoResource {

    @Inject
    jakarta.ws.rs.core.SecurityContext securityContext;

    @Inject
    TelemetriaService telemetriaService;

    @POST
    @Path("simular-investimento")
    @Timed(value = "simulacao.processamento", description = "Tempo para realizar uma simulação")
    @Transactional
    public Response simular(SimulacaoDTO.Request request) {

        long inicio = System.currentTimeMillis();

        try {

            ProdutoInvestimento produto = ProdutoInvestimento.find("tipo", request.tipoProduto).firstResult();
            if (produto == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Nenhum produto encontrado para o tipo: " + request.tipoProduto).build();
            }

            String username = securityContext.getUserPrincipal().getName();
            Usuario usuario = Usuario.findByUsername(username);

            if (usuario == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            // ------ CÁLCULOS ---
            BigDecimal taxaAnual = produto.rentabilidadeAnual.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
            double taxaMensalDouble = Math.pow(1 + taxaAnual.doubleValue(), 1.0 / 12.0) - 1;
            BigDecimal taxaMensal = BigDecimal.valueOf(taxaMensalDouble);
            BigDecimal fatorCrescimento = taxaMensal.add(BigDecimal.ONE).pow(request.prazoMeses);
            BigDecimal valorFinal = request.valor.multiply(fatorCrescimento).setScale(2, RoundingMode.HALF_UP);

            Simulacao novaSimulacao = new Simulacao();

            novaSimulacao.clienteId = usuario.getId();
            novaSimulacao.valorInvestido = request.valor;
            novaSimulacao.valorFinal = valorFinal;
            novaSimulacao.prazoMeses = request.prazoMeses;
            novaSimulacao.produtoNome = produto.nome;
            novaSimulacao.produtoTipo = produto.tipo;
            novaSimulacao.dataSimulacao = LocalDateTime.now();
            novaSimulacao.persistAndFlush();

            SimulacaoDTO.ProdutoInfo produtoInfo = new SimulacaoDTO.ProdutoInfo();
            produtoInfo.id = produto.getId();
            produtoInfo.nome = produto.nome;
            produtoInfo.tipo = produto.tipo;
            produtoInfo.rentabilidade = produto.rentabilidadeAnual;
            produtoInfo.risco = produto.risco.name();

            SimulacaoDTO.ResultadoSimulacao resultado = new SimulacaoDTO.ResultadoSimulacao();
            resultado.valorFinal = valorFinal;
            resultado.prazoMeses = request.prazoMeses;
            resultado.rentabilidadeEfetiva = valorFinal.subtract(request.valor)
                    .divide(request.valor, 4, RoundingMode.HALF_UP);

            return Response.ok(new SimulacaoDTO.Response(produtoInfo, resultado)).build();
        } finally {
            long fim = System.currentTimeMillis();
            telemetriaService.registrar("simular-investimento", fim - inicio);
        }
    }

    @GET
    @Path("simulacoes")
    public List<Simulacao> listarSimulacoes() {

        // Se for ADMIN, retorna tudo
        if (securityContext.isUserInRole("admin")) {
            return Simulacao.listAll();
        }

        // Se for USER, descobre o ID e filtra
        String username = securityContext.getUserPrincipal().getName();
        Usuario usuario = Usuario.findByUsername(username);

        if (usuario == null) {
            throw new WebApplicationException("Utilizador não encontrado", 404);
        }

        return Simulacao.list("clienteId", usuario.getId());
    }


    @GET
    @Path("simulacoes/por-produto-dia")
    @RolesAllowed("admin")
    public List<SimulacaoDTO.AgregadoProdutoDia> listarAgrupadoPorProdutoDia() {
        List<Simulacao> todas = Simulacao.listAll();

        Map<String, Map<LocalDate, List<Simulacao>>> agrupamento = todas.stream()
                .filter(s -> s != null)
                .filter(s -> s.produtoNome != null)
                .filter(s -> s.dataSimulacao != null)
                .collect(Collectors.groupingBy(
                        s -> s.produtoNome,
                        Collectors.groupingBy(
                                s -> s.dataSimulacao.toLocalDate()
                        )
                ));

        return agrupamento.entrySet().stream()
                .flatMap(entryProduto -> {
                    String produtoNome = entryProduto.getKey();
                    Map<LocalDate, List<Simulacao>> porData = entryProduto.getValue();

                    return porData.entrySet().stream().map(entryData -> {
                        LocalDate data = entryData.getKey();
                        List<Simulacao> lista = entryData.getValue();

                        long quantidade = lista.size();

                        // Tratamento seguro para BigDecimal
                        BigDecimal somaValorFinal = lista.stream()
                                .map(s -> s.valorFinal != null ? s.valorFinal : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal media = somaValorFinal.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP);

                        return new SimulacaoDTO.AgregadoProdutoDia(produtoNome, data, quantidade, media);
                    });
                })
                .collect(Collectors.toList());
    }
}