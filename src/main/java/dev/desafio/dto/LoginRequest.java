package dev.desafio.dto;

import jakarta.validation.constraints.NotBlank;

// Um DTO (Data Transfer Object) para encapsular os dados do login
public class LoginRequest {
    @NotBlank(message = "username é obrigatório")
    public String username;

    @NotBlank(message = "password é obrigatório")
    public String password;
}