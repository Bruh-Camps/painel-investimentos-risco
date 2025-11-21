package dev.desafio.config;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Erro capturado: ", exception);

        int code = 500;
        String mensagem = "Erro interno do servidor. Contacte o suporte.";

        if (exception instanceof WebApplicationException webEx) {
            code = webEx.getResponse().getStatus();
            mensagem = exception.getMessage();
        } else if (exception instanceof IllegalArgumentException) {
            code = 400;
            mensagem = exception.getMessage();
        }

        // Retorna sempre um JSON limpo
        return Response.status(code)
                .entity(Map.of(
                        "status", code,
                        "erro", mensagem
                ))
                .build();
    }
}