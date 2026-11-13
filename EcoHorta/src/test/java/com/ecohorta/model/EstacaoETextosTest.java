package com.ecohorta.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstacaoETextosTest {

    @Test
    void cadaMesPertenceAExatamenteUmaEstacao() {
        for (int mes = 1; mes <= 12; mes++) {
            int mesAtual = mes;
            long quantas = List.of(Estacao.values()).stream().filter(e -> e.contem(mesAtual)).count();
            assertEquals(1L, quantas, "mês " + mes);
        }
    }

    @Test
    void deMesDevolveAEstacaoDoHemisferioSul() {
        assertEquals(Estacao.VERAO, Estacao.deMes(12));
        assertEquals(Estacao.VERAO, Estacao.deMes(1));
        assertEquals(Estacao.OUTONO, Estacao.deMes(4));
        assertEquals(Estacao.INVERNO, Estacao.deMes(7));
        assertEquals(Estacao.PRIMAVERA, Estacao.deMes(10));
        assertThrows(IllegalArgumentException.class, () -> Estacao.deMes(13));
    }

    @Test
    void estacaoTemNomeOrdemEMeses() {
        assertEquals("Verão", Estacao.VERAO.getNome());
        assertEquals(1, Estacao.PRIMAVERA.getOrdem());
        assertEquals(List.of(12, 1, 2), Estacao.VERAO.getMeses());
    }

    @Test
    void nomesDosMeses() {
        assertEquals("Janeiro", Meses.nome(1));
        assertEquals("Dezembro", Meses.nome(12));
        assertThrows(IllegalArgumentException.class, () -> Meses.nome(0));
    }

    @Test
    void normalizarRemoveAcentosMaiusculasEEspacos() {
        assertEquals("acafrao", Textos.normalizar("  Açafrão "));
        assertEquals("couve flor", Textos.normalizar("COUVE   Flor"));
        assertEquals("", Textos.normalizar(null));
        assertEquals("", Textos.normalizar("   "));
    }

    @Test
    void novosEnumsTemRotulos() {
        assertEquals("Pouca luz", NivelLuz.MINIMA.getRotulo());
        assertEquals(2, NivelLuz.MINIMA.getHorasMinimas());
        assertEquals(4, NivelLuz.MEDIA.getHorasMinimas());
        assertEquals(6, NivelLuz.PLENA.getHorasMinimas());
        assertTrue(NivelLuz.MEDIA.getRotulo().contains("meio dia"));
        assertTrue(NivelLuz.PLENA.getDescricao().contains("6 horas"));
        assertEquals("Ideal para apartamento", NivelApartamento.IDEAL.getRotulo());
        assertEquals("Folhosa", CategoriaPlanta.FOLHOSA.getRotulo());
        assertEquals("Grave", Gravidade.GRAVE.getRotulo());
        assertEquals("Pessoas", TipoRegistroSaude.PESSOA.getRotulo());
    }

    @Test
    void adaptacaoApartamentoExigeNivel() {
        assertThrows(IllegalArgumentException.class, () -> new AdaptacaoApartamento(null, ""));
        assertEquals("", new AdaptacaoApartamento(NivelApartamento.IDEAL, null).observacao());
        assertFalse(new AdaptacaoApartamento(NivelApartamento.IDEAL, " x ").observacao().isBlank());
    }
}
