package dev.desafio.resource;

import dev.desafio.service.PerfilRiscoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
public class PerfilRiscoResource {

    @Inject
    PerfilRiscoService perfilService;

    @GET
    @Path("/{clienteId}")
    @RolesAllowed({"user", "admin"}) // Permite que o utilizador ou um administrador consultem
    public Response obterPerfil(@PathParam("clienteId") Long clienteId) {

        // 1. Chama o serviço para calcular o perfil
        PerfilRiscoService.PerfilCalculado resultado = perfilService.calcularPerfil(clienteId);

        // 2. Monta o DTO de resposta (Record) para o JSON
        PerfilResponse response = new PerfilResponse(
                clienteId,
                resultado.perfil(),
                resultado.pontuacao(),
                resultado.descricao()
        );

        return Response.ok(response).build();
    }

    // Record auxiliar para formatar o JSON de saída
    public record PerfilResponse(
            Long clienteId,
            String perfil,
            int pontuacao,
            String descricao
    ) {}
}