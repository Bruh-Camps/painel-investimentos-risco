package dev.desafio.resource;

import dev.desafio.entity.HistoricoInvestimento;
import dev.desafio.entity.Usuario;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.inject.Inject;
import java.util.List;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoricoInvestimentoResource {

    @Inject
    JsonWebToken jwt; // Para pegar o usuário logado

    @GET
    @Path("/{clienteId}")
    @RolesAllowed("admin") // Ex: Só admin pode ver a carteira de qualquer um
    public List<HistoricoInvestimento> getHistoricoAdmin(@PathParam("clienteId") Long clienteId) {
        return HistoricoInvestimento.list("clienteId", clienteId);
    }

    @GET
    @Path("/minha")
    @RolesAllowed("user")
    public List<HistoricoInvestimento> getMeusInvestimentos() {
        String username = jwt.getName();
        Usuario usuario = Usuario.findByUsername(username);

        if (usuario == null) {
            throw new WebApplicationException("Utilizador não encontrado", 404);
        }

        return HistoricoInvestimento.list("clienteId", usuario.getId());
    }
}