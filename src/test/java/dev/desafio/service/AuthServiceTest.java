package dev.desafio.service;

import dev.desafio.entity.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class AuthServiceTest {

    @Inject
    AuthService authService;

    @Test
    public void testeAuthenticateSucesso() {
        // 1. Preparar um utilizador FAKE com senha hashada
        String senhaPlana = "minhaSenha123";
        String senhaHash = BcryptUtil.bcryptHash(senhaPlana);

        Usuario usuarioMock = new Usuario();
        usuarioMock.username = "testeUser";
        usuarioMock.password = senhaHash;
        usuarioMock.role = "user";

        // 2. Mockar o banco de dados para retornar este utilizador
        PanacheMock.mock(Usuario.class);
        Mockito.when(Usuario.findByUsername("testeUser"))
                .thenReturn(usuarioMock);

        // 3. Testar login com a senha correta
        Usuario resultado = authService.authenticate("testeUser", senhaPlana);

        // 4. Deve retornar o utilizador (Sucesso)
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("testeUser", resultado.username);
    }

    @Test
    public void testeAuthenticateSenhaIncorreta() {
        // 1. Preparar utilizador
        String senhaHash = BcryptUtil.bcryptHash("senhaCerta");
        Usuario usuarioMock = new Usuario();
        usuarioMock.username = "testeUser";
        usuarioMock.password = senhaHash;

        // 2. Mockar banco
        PanacheMock.mock(Usuario.class);
        Mockito.when(Usuario.findByUsername("testeUser"))
                .thenReturn(usuarioMock);

        // 3. Testar com senha ERRADA
        Usuario resultado = authService.authenticate("testeUser", "senhaErrada");

        // 4. Deve retornar null (Falha)
        Assertions.assertNull(resultado);
    }

    @Test
    public void testeAuthenticateUsuarioNaoEncontrado() {
        // 1. Mockar banco retornando null
        PanacheMock.mock(Usuario.class);
        Mockito.when(Usuario.findByUsername("fantasma"))
                .thenReturn(null);

        // 2. Testar
        Usuario resultado = authService.authenticate("fantasma", "123456");

        // 3. Deve retornar null
        Assertions.assertNull(resultado);
    }

    @Test
    public void testeGenerateToken() {
        // 1. Criar utilizador simples
        Usuario usuario = new Usuario();
        usuario.username = "userToken";
        usuario.role = "admin";

        // 2. Gerar token
        String token = authService.generateToken(usuario);

        // 3. Validar se gerou algo parecido com JWT
        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.length() > 20);
        // Opcional: verificar se tem 3 partes separadas por pontos (header.payload.sig)
        Assertions.assertEquals(3, token.split("\\.").length);
    }
}