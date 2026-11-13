package com.ecohorta.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Valida as regras dos objetos de valor (records) usados como subdocumentos. */
class ValueObjectsTest {

    @Test
    void regaRecomendadaValidaFrequenciaEQuantidade() {
        RegaRecomendada rega = new RegaRecomendada(2, 500, "  base da planta ");
        assertEquals("base da planta", rega.observacao());
        assertEquals("", new RegaRecomendada(1, 100, null).observacao());
        assertThrows(IllegalArgumentException.class, () -> new RegaRecomendada(0, 100, ""));
        assertThrows(IllegalArgumentException.class, () -> new RegaRecomendada(1, 0, ""));
    }

    @Test
    void cuidadoPeriodicoDependeDaFrequencia() {
        assertTrue(new Cuidado(TipoCuidado.ADUBACAO, "Adubar", 15).periodico());
        assertFalse(new Cuidado(TipoCuidado.SOLO, "Solo fofo", 0).periodico());
    }

    @Test
    void cuidadoValidaCampos() {
        assertThrows(IllegalArgumentException.class, () -> new Cuidado(null, "x", 1));
        assertThrows(IllegalArgumentException.class, () -> new Cuidado(TipoCuidado.PODA, " ", 1));
        assertThrows(IllegalArgumentException.class, () -> new Cuidado(TipoCuidado.PODA, null, 1));
        assertThrows(IllegalArgumentException.class, () -> new Cuidado(TipoCuidado.PODA, "x", -1));
    }

    @Test
    void canteiroNormalizaEValidaCampos() {
        Canteiro canteiro = new Canteiro("  Canteiro 1 ", 3.5, " ");
        assertEquals("Canteiro 1", canteiro.nome());
        assertEquals("Não informada", canteiro.exposicaoSol());
        assertEquals("Não informada", new Canteiro("A", 1, null).exposicaoSol());
        assertEquals("Sol pleno", new Canteiro("A", 1, " Sol pleno ").exposicaoSol());
        assertThrows(IllegalArgumentException.class, () -> new Canteiro(" ", 1, ""));
        assertThrows(IllegalArgumentException.class, () -> new Canteiro(null, 1, ""));
        assertThrows(IllegalArgumentException.class, () -> new Canteiro("A", 0, ""));
    }

    @Test
    void registrosValidamCampos() {
        LocalDate dia = LocalDate.of(2026, 11, 10);
        assertEquals("", new RegistroRega(dia, 200, null).observacao());
        assertThrows(IllegalArgumentException.class, () -> new RegistroRega(null, 200, ""));
        assertThrows(IllegalArgumentException.class, () -> new RegistroRega(dia, 0, ""));

        assertEquals("", new RegistroCuidado(dia, TipoCuidado.PODA, null).descricao());
        assertThrows(IllegalArgumentException.class, () -> new RegistroCuidado(null, TipoCuidado.PODA, ""));
        assertThrows(IllegalArgumentException.class, () -> new RegistroCuidado(dia, null, ""));
    }

    @Test
    void enumsTemRotulosEmPortugues() {
        assertEquals("Ativo", StatusPlantio.ATIVO.getRotulo());
        assertEquals("Colhido", StatusPlantio.COLHIDO.getRotulo());
        assertEquals("Perdido", StatusPlantio.PERDIDO.getRotulo());
        assertEquals("Adubação", TipoCuidado.ADUBACAO.getRotulo());
        assertEquals("Rega", TipoTarefa.REGA.getRotulo());
        assertEquals("Colheita", TipoTarefa.COLHEITA.getRotulo());
    }
}
