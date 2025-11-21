package dev.desafio.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProdutoInvestimentoResourceTest {

    @Test
    @TestSecurity(user = "user123", roles = "user")
    public void testeListarCatalogoCompleto() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/catalogo")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(5)); // O import.sql tem 5 produtos
    }

    @Test
    @TestSecurity(user = "user123", roles = "user")
    public void testeRecomendacaoConservador() {
        // Perfil Conservador deve ver apenas risco BAIXO
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/produtos-recomendados/CONSERVADOR")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                // Garante que TODOS os itens retornados são BAIXO
                .body("risco", everyItem(is("BAIXO")));
    }

    @Test
    @TestSecurity(user = "user123", roles = "user")
    public void testeRecomendacaoAgressivo() {
        // Perfil Agressivo vê tudo (Baixo, Médio e Alto)
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/produtos-recomendados/AGRESSIVO")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(3))
                .body("risco", hasItems("BAIXO", "MEDIO", "ALTO"));
    }
}
