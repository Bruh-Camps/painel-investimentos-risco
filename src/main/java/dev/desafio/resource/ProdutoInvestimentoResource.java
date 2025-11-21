package dev.desafio.resource;

import dev.desafio.entity.NivelRisco;
import dev.desafio.entity.ProdutoInvestimento;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoInvestimentoResource {

    @GET
    @Path("catalogo")
    @RolesAllowed({"user", "admin"})
    public List<ProdutoInvestimento> listarCatalogo() {
        return ProdutoInvestimento.listAll(Sort.by("risco").and("nome"));
    }

    @GET
    @Path("produtos-recomendados/{perfil}")
    @RolesAllowed({"user", "admin"})
    public List<ProdutoInvestimento> recomendarProdutos(@PathParam("perfil") String perfil) {

        String perfilNormalizado = perfil.trim().toUpperCase();

        List<NivelRisco> riscosPermitidos = new ArrayList<>();

        switch (perfilNormalizado) {
            case "CONSERVADOR":
                riscosPermitidos.add(NivelRisco.BAIXO);
                break;
            case "MODERADO":
                riscosPermitidos.add(NivelRisco.BAIXO);
                riscosPermitidos.add(NivelRisco.MEDIO);
                break;
            case "AGRESSIVO":
                riscosPermitidos.add(NivelRisco.BAIXO);
                riscosPermitidos.add(NivelRisco.MEDIO);
                riscosPermitidos.add(NivelRisco.ALTO);
                break;
            default:
                // Se o perfil não for reconhecido, retorna apenas BAIXO por segurança
                riscosPermitidos.add(NivelRisco.BAIXO);
        }

        return ProdutoInvestimento.list("risco IN ?1", riscosPermitidos);
    }
}