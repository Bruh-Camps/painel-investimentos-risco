package dev.desafio.service;

import dev.desafio.entity.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public String generateToken(Usuario usuario) {
        Set<String> roles = new HashSet<>();
        roles.add(usuario.role);

        return Jwt.issuer(issuer)
                .upn(usuario.username) // identifica o usuário
                .groups(roles)         // roles/perfis
                .expiresIn(Duration.ofHours(2)) // expiração mais segura
                .sign();
    }

    public Usuario authenticate(String username, String password) {
        Usuario usuario = Usuario.findByUsername(username);

        if (usuario != null && BcryptUtil.matches(password, usuario.password)) {
            return usuario;
        }

        return null;
    }
}