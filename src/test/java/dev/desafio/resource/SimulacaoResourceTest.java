package dev.desafio.resource;

import dev.desafio.dto.SimulacaoDTO;
import dev.desafio.entity.Simulacao;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class SimulacaoResourceTest {
    @BeforeEach
    @Transactional
    public void setupDados() {
        // Limpa simulações anteriores para não sujar os testes
        Simulacao.deleteAll();

        // Cria Simulação do Usuário 1 (user123)
        Simulacao s1 = new Simulacao();
        s1.clienteId = 1L; // ID do user123 (ver import.sql)
        s1.valorInvestido = new BigDecimal("1000");
        s1.valorFinal = new BigDecimal("1100");
        s1.produtoNome = "Produto User";
        s1.dataSimulacao = LocalDateTime.now();
        s1.persist();

        // Cria Simulação do Usuário 2 (admin123 ou outro)
        Simulacao s2 = new Simulacao();
        s2.clienteId = 2L; // ID de outro usuário
        s2.valorInvestido = new BigDecimal("5000");
        s2.valorFinal = new BigDecimal("5500");
        s2.produtoNome = "Produto Outro";
        s2.dataSimulacao = LocalDateTime.now();
        s2.persist();
    }

    @Test
    @TestSecurity(user = "user123", roles = "user") // Simula utilizador autenticado
    public void testeSimularInvestimentoSucesso() {

        // Preparar o JSON de requisição
        SimulacaoDTO.Request request = new SimulacaoDTO.Request();
        request.valor = new BigDecimal("1000.00");
        request.prazoMeses = 12;
        request.tipoProduto = "CDB"; // Tipo existente no import.sql

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200)
                .body("produtoValidado.tipo", equalTo("CDB"))
                .body("resultadoSimulacao.valorFinal", notNullValue());
    }

    @Test
    public void testeSimularSemLoginFalha() {
        // Sem @TestSecurity -> Não autenticado

        SimulacaoDTO.Request request = new SimulacaoDTO.Request();
        request.valor = new BigDecimal("1000.00");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(401); // Espera Unauthorized
    }

    @Test
    @TestSecurity(user = "user123", roles = "user") // Utilizador válido
    public void testeSimularProdutoInexistente() {

        SimulacaoDTO.Request request = new SimulacaoDTO.Request();
        request.valor = new BigDecimal("5000.00");
        request.prazoMeses = 6;
        request.tipoProduto = "CRYPTO"; // Tipo que NÃO existe no import.sql

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(400) // Espera Bad Request
                // Opcional: Verifica se a mensagem de erro está correta
                .body(containsString("Nenhum produto encontrado"));
    }

    @Test
    @TestSecurity(user = "fantasma", roles = "user") // Token válido, mas utilizador não está no Banco
    public void testeSimularUsuarioNaoEncontrado() {

        SimulacaoDTO.Request request = new SimulacaoDTO.Request();
        request.valor = new BigDecimal("1000.00");
        request.prazoMeses = 12;
        request.tipoProduto = "CDB"; // Produto válido

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(401); // Espera Unauthorized porque o 'fantasma' não está na tabela Usuario
    }

    @Test
    @TestSecurity(user = "user123", roles = "user")
    public void testeListarSimulacoesComoUser() {
        // O user123 (ID 1) só deve ver a SUA simulação (s1)
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("size()", is(1)) // Só deve vir 1 registro
                .body("[0].clienteId", is(1)) // Garante que o ID é dele
                .body("[0].produtoNome", is("Produto User"));
    }

    @Test
    @TestSecurity(user = "admin123", roles = "admin")
    public void testeListarSimulacoesComoAdmin() {
        // O Admin deve ver TUDO (s1 e s2)
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("size()", is(2)); // Deve ver os 2 registros
    }

    @Test
    @TestSecurity(user = "admin123", roles = "admin")
    public void testeRelatorioAgrupadoComoAdmin() {
        // O Admin acessa o relatório global
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].produto", notNullValue())
                .body("[0].mediaValorFinal", notNullValue());
    }

    @Test
    @TestSecurity(user = "user123", roles = "user")
    public void testeRelatorioAgrupadoComoUser() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(403);
    }
    @Test

    @TestSecurity(user = "usuarioFantasma", roles = "user")
    public void testeListarSimulacoesUsuarioNaoEncontrado() {

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(404); // Espera o 404 lançado pela WebApplicationException
    }
}