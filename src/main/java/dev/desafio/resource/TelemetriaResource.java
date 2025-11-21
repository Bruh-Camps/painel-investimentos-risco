package dev.desafio.resource;

import dev.desafio.dto.TelemetriaDTO;
import dev.desafio.service.TelemetriaService;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

    // Variável para guardar quando o servidor subiu
    private LocalDateTime dataInicioAplicacao;

    // Captura o evento de inicialização do Quarkus
    @PermitAll
    void onStart(@Observes StartupEvent ev) {
        this.dataInicioAplicacao = LocalDateTime.now();
    }

    @GET
    public TelemetriaDTO obterTelemetria() {
        List<TelemetriaDTO.ServicoMetrica> metricas = new ArrayList<>();

        var dadosSimulacao = telemetriaService.ler("simular-investimento");
        metricas.add(new TelemetriaDTO.ServicoMetrica(
                "simular-investimento",
                dadosSimulacao.getQuantidade(),
                dadosSimulacao.getMedia()
        ));

        var dadosPerfil = telemetriaService.ler("perfil-risco");
        metricas.add(new TelemetriaDTO.ServicoMetrica(
                "perfil-risco",
                dadosPerfil.getQuantidade(),
                dadosPerfil.getMedia()
        ));

        return new TelemetriaDTO(metricas, dataInicioAplicacao, LocalDateTime.now());
    }
}