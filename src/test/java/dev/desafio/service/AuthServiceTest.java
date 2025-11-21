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
        // Prepara um utilizador FAKE com senha hashada
        String senhaPlana = "minhaSenha123";
        String senhaHash = BcryptUtil.bcryptHash(senhaPlana);

        Usuario usuarioMock = new Usuario();
        usuarioMock.username = "testeUser";
        usuarioMock.password = senhaHash;
        usuarioMock.role = "user";

        PanacheMock.mock(Usuario.class);
        Mockito.when(Usuario.findByUsername("testeUser"))
                .thenReturn(usuarioMock);

        // Testar login com a senha correta
        Usuario resultado = authService.authenticate("testeUser", senhaPlana);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("testeUser", resultado.username);
    }

    @Test
    public void testeAuthenticateSenhaIncorreta() {
        String senhaHash = BcryptUtil.bcryptHash("senhaCerta");
        Usuario usuarioMock = new Usuario();
        usuarioMock.username = "testeUser";
        usuarioMock.password = senhaHash;

        PanacheMock.mock(Usuario.class);
        Mockito.when(Usuario.findByUsername("testeUser"))
                .thenReturn(usuarioMock);

        // 3. Testar com senha ERRADA
        Usuario resultado = authService.authenticate("testeUser", "senhaErrada");

        Assertions.assertNull(resultado);
    }

    @Test
    public void testeAuthenticateUsuarioNaoEncontrado() {
        PanacheMock.mock(Usuario.class);
        Mockito.when(Usuario.findByUsername("fantasma"))
                .thenReturn(null);

        Usuario resultado = authService.authenticate("fantasma", "123456");

        Assertions.assertNull(resultado);
    }

    @Test
    public void testeGenerateToken() {
        Usuario usuario = new Usuario();
        usuario.username = "userToken";
        usuario.role = "admin";

        String token = authService.generateToken(usuario);

        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.length() > 20);
        Assertions.assertEquals(3, token.split("\\.").length);
    }
}