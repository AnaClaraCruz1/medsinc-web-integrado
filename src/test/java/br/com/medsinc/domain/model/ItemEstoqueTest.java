package br.com.medsinc.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemEstoqueTest {

    @Test
    void deveCriarItemComDadosValidos() {
        ItemEstoque item = new ItemEstoque(1, "Luvas", 12, "caixas", 5, 1);

        assertEquals("Luvas", item.nome());
        assertEquals(12, item.quantidade());
        assertEquals(5, item.quantidadeMinima());
    }

    @Test
    void deveRejeitarNomeNuloOuEmBranco() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemEstoque(1, null, 10, "unidades", 2, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemEstoque(1, " ", 10, "unidades", 2, 1));
    }

    @Test
    void deveRejeitarQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemEstoque(1, "Luvas", -1, "caixas", 5, 1));
    }

    @Test
    void deveRejeitarQuantidadeMinimaNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemEstoque(1, "Luvas", 10, "caixas", -1, 1));
    }

    @Test
    void deveExigirUsuarioResponsavelPositivo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemEstoque(1, "Luvas", 10, "caixas", 5, 0));
    }

    @Test
    void deveIdentificarQuantidadeIgualAoMinimoComoAlerta() {
        ItemEstoque item = new ItemEstoque(1, "Luvas", 5, "caixas", 5, 1);

        assertTrue(item.noLimiteOuAbaixoDoMinimo());
    }

    @Test
    void deveIdentificarQuantidadeAbaixoDoMinimoComoAlerta() {
        ItemEstoque item = new ItemEstoque(1, "Luvas", 4, "caixas", 5, 1);

        assertTrue(item.noLimiteOuAbaixoDoMinimo());
    }

    @Test
    void naoDeveAlertarQuandoQuantidadeEstaAcimaDoMinimo() {
        ItemEstoque item = new ItemEstoque(1, "Luvas", 6, "caixas", 5, 1);

        assertFalse(item.noLimiteOuAbaixoDoMinimo());
    }
}
