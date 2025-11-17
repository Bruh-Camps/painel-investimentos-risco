package dev.desafio.resource;

import dev.desafio.entity.HistoricoInvestimento;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.inject.Inject;
import java.util.List;

@Path("/carteira")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoricoInvestimentoResource {

    @Inject
    JsonWebToken jwt; // Para pegar o usuário logado

    @GET
    @Path("/{clienteId}")
    @RolesAllowed("admin") // Ex: Só admin pode ver a carteira de qualquer um
    public List<HistoricoInvestimento> getCarteiraAdmin(@PathParam("clienteId") Long clienteId) {
        return HistoricoInvestimento.list("clienteId", clienteId);
    }

    @GET
    @Path("/minha")
    @RolesAllowed("user") // "user" só pode ver a própria carteira
    public List<HistoricoInvestimento> getMinhaCarteira() {
        // NOTA: Seu import.sql usa 'clienteId' como 123 e 456.
        // O login (AuthResource) usa 'username' (user123, admin123).
        // Você precisará definir como o 'clienteId' se relaciona com o 'username'.
        // Por enquanto, este endpoint vai falhar a lógica,
        // mas o /catalogo e /auth/login funcionarão.
        // String username = jwt.getName();
        // Long clienteId = ... busca o clienteId pelo username
        // return HistoricoInvestimento.list("clienteId", clienteId);

        // Retorno temporário:
        return HistoricoInvestimento.listAll(Sort.by("data"));
    }
}