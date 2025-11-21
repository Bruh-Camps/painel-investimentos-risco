package dev.desafio.resource;

import dev.desafio.dto.LoginRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class AuthResourceTest {

    @Test
    public void testeLoginSucesso() {
        // Tenta logar com o usuário criado no import.sql
        LoginRequest login = new LoginRequest();
        login.username = "user123";
        login.password = "user123";

        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue()); // Deve retornar o token JWT
    }

    @Test
    public void testeLoginSenhaIncorreta() {
        LoginRequest login = new LoginRequest();
        login.username = "user123";
        login.password = "senha_errada";

        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401); // Unauthorized
    }
}