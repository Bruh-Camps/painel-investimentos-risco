package dev.desafio.resource;

import dev.desafio.entity.HistoricoInvestimento;
import dev.desafio.entity.Usuario;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class PerfilRiscoResourceTest {

    @BeforeEach
    @Transactional
    public void setup() {
        // Limpa histórico para garantir teste limpo
        HistoricoInvestimento.deleteAll();

        // Garante que o utilizador 1 existe (já vem do import.sql, mas por segurança)
        if (Usuario.findById(1L) == null) {
            Usuario u = new Usuario();
            u.setId(1L);
            u.username = "user123";
            u.persist();
        }
    }

    @Test
    @TestSecurity(user = "user123", roles = "user")
    public void testeEndpointPerfilConservadorPadrao() {
        // Sem histórico inserido -> deve ser Conservador
        given()
                .when()
                .get("/perfil-risco/1") // ID do user123
                .then()
                .statusCode(200)
                .body("perfil", equalTo("Conservador"));
    }

    @Test
    @TestSecurity(user = "user123", roles = "user")
    public void testeEndpointPerfilAgressivo() {

        // 1. Abrir uma transação curta apenas para inserir e COMMITAR logo em seguida
        QuarkusTransaction.requiringNew().run(() -> {
            HistoricoInvestimento inv = new HistoricoInvestimento();
            inv.clienteId = 1L;
            inv.tipo = "Ações";
            inv.valor = new BigDecimal("100000"); // Volume alto
            inv.rentabilidade = BigDecimal.ZERO;
            inv.data = "2025-01-01";
            inv.persist();
        });

        // 2. Agora que o dado está comitado no banco, chamamos a API
        given()
                .when()
                .get("/perfil-risco/1")
                .then()
                .statusCode(200)
                .body("perfil", equalTo("Agressivo"));
    }
}