package dev.desafio.tools;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/tools")
public class ToolsResource {

    @GET
    @Path("/hash/{password}")
    public String hash(@PathParam("password") String password) {
        return BcryptUtil.bcryptHash(password);
    }
}
