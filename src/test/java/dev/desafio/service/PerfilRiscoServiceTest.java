package dev.desafio.service;

import dev.desafio.entity.HistoricoInvestimento;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@QuarkusTest
public class PerfilRiscoServiceTest {

    @Inject
    PerfilRiscoService perfilService;

    @Test
    public void deveRetornarPerfilConservadorSemHistorico() {
        // MOCK: Simula que o banco de dados não retorna nada para este cliente
        PanacheMock.mock(HistoricoInvestimento.class);
        Mockito.when(HistoricoInvestimento.list("clienteId", 1L))
                .thenReturn(List.of());

        // EXECUÇÃO
        var resultado = perfilService.calcularPerfil(1L);

        // VALIDAÇÃO
        Assertions.assertEquals("Conservador", resultado.perfil());
        Assertions.assertEquals(0, resultado.pontuacao());
    }

    @Test
    public void deveRetornarPerfilAgressivoComAcoes() {
        // CENÁRIO: Criar um histórico fake com Ações e alto volume
        HistoricoInvestimento inv1 = new HistoricoInvestimento();
        inv1.tipo = "Ações Petrobras";
        inv1.valor = new BigDecimal("50000"); // Alto volume

        HistoricoInvestimento inv2 = new HistoricoInvestimento();
        inv2.tipo = "Fundo Ações";
        inv2.valor = new BigDecimal("50000");

        // MOCK: Quando o serviço pedir o histórico, devolvemos nossa lista fake
        PanacheMock.mock(HistoricoInvestimento.class);
        Mockito.when(HistoricoInvestimento.list("clienteId", 2L))
                .thenReturn(List.of(inv1, inv2));

        // EXECUÇÃO
        var resultado = perfilService.calcularPerfil(2L);

        // VALIDAÇÃO
        // Pontos esperados:
        // +20 (Tipo Ações) + 20 (Tipo Fundo Ações) = 40
        // +50 (Volume 100k / 10k * 5) = 50
        // +4 (Frequência 2 * 2) = 4
        // Total: 94 pontos -> Agressivo (>50)
        Assertions.assertEquals("Agressivo", resultado.perfil());
        Assertions.assertTrue(resultado.pontuacao() > 50);
    }

    @Test
    public void testeCalculoPerfilSemHistorico() {
        // CENÁRIO: Cliente sem investimentos
        PanacheMock.mock(HistoricoInvestimento.class);
        Mockito.when(HistoricoInvestimento.list("clienteId", 1L))
                .thenReturn(Collections.emptyList());

        // EXECUÇÃO
        var resultado = perfilService.calcularPerfil(1L);

        // VALIDAÇÃO: Deve ser Conservador (regra padrão)
        Assertions.assertEquals("Conservador", resultado.perfil());
        Assertions.assertEquals(0, resultado.pontuacao());
    }

    @Test
    public void testeCalculoPerfilAgressivo() {
        // CENÁRIO: Cliente com alto volume e ações

        HistoricoInvestimento inv1 = new HistoricoInvestimento();
        inv1.tipo = "Ações Petrobras";
        inv1.valor = new BigDecimal("300000");

        HistoricoInvestimento inv2 = new HistoricoInvestimento();
        inv2.tipo = "Fundo Imobiliário";
        inv2.valor = new BigDecimal("15000");

        PanacheMock.mock(HistoricoInvestimento.class);
        Mockito.when(HistoricoInvestimento.list("clienteId", 2L))
                .thenReturn(List.of(inv1, inv2));

        var resultado = perfilService.calcularPerfil(2L);

        Assertions.assertEquals("Agressivo", resultado.perfil());
        Assertions.assertTrue(resultado.pontuacao() >= 50);
    }

    @Test
    public void testeCalculoPerfilModerado() {
        // CENÁRIO: Cliente com renda fixa simples mas algum volume
        HistoricoInvestimento inv1 = new HistoricoInvestimento();
        inv1.tipo = "CDB"; // +2 pts
        inv1.valor = new BigDecimal("20000"); // +10 pts (2 * 5)

        PanacheMock.mock(HistoricoInvestimento.class);
        Mockito.when(HistoricoInvestimento.list("clienteId", 3L))
                .thenReturn(List.of(inv1));

        // Pontos: 2 (tipo) + 10 (volume) + 2 (freq) = 14 pts
        // Ops, < 20 é Conservador. Vamos adicionar mais um investimento.

        HistoricoInvestimento inv2 = new HistoricoInvestimento();
        inv2.tipo = "FII"; // +10 pts
        inv2.valor = new BigDecimal("5000"); // +0 pts volume extra significativo

        Mockito.when(HistoricoInvestimento.list("clienteId", 3L))
                .thenReturn(List.of(inv1, inv2));

        // Novo Total:
        // Tipos: 2 (CDB) + 10 (FII) = 12
        // Volume Total: 25.000 -> 10 pts
        // Frequência: 4 pts
        // TOTAL: 26 pts -> Moderado (>= 20 e < 50)

        var resultado = perfilService.calcularPerfil(3L);

        Assertions.assertEquals("Moderado", resultado.perfil());
    }
}