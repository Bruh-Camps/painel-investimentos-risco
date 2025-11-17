package dev.desafio.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
@UserDefinition
public class Usuario extends PanacheEntity {

    @Username
    @Column(unique = true)
    public String username;

    @Password
    public String password;

    @Roles
    public String role;

    public static Usuario findByUsername(String username) {
        return find("username", username).firstResult();
    }
}