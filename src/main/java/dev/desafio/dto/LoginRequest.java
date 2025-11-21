package dev.desafio.dto;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class LoginRequest {
    @NotBlank(message = "username é obrigatório")
    @Schema(description = "Nome de utilizador registado", examples = {"admin123"})
    public String username;

    @NotBlank(message = "password é obrigatório")
    @Schema(description = "Senha do utilizador", examples = {"admin123"})
    public String password;
}