package dev.desafio.resource;

import dev.desafio.entity.ProdutoInvestimento;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/catalogo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoInvestimentoResource {

    @GET
    @RolesAllowed({"user", "admin"}) // Protegido: só logado pode ver
    public List<ProdutoInvestimento> listarCatalogo() {
        return ProdutoInvestimento.listAll(Sort.by("risco").and("nome"));
    }
}
