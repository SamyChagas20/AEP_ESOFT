package com.ecohorta.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempoESaudeModelTest {

    // ---------------- Tempo ----------------

    @Test
    void tempoGuardaEstacaoMelhorMesEMeses() {
        Tempo tempo = new Tempo(Estacao.PRIMAVERA, " Clima bom ", 10, " Porque sim ")
                .adicionarMes(9, "frio")
                .adicionarMes(10, "ideal")
                .adicionarMes(11, "calor");
        tempo.setId("t1");

        assertEquals("t1", tempo.getId());
        assertEquals(Estacao.PRIMAVERA, tempo.getEstacao());
        assertEquals("Clima bom", tempo.getDescricao());
        assertEquals("Porque sim", tempo.getJustificativaMelhorMes());
        assertEquals(3, tempo.getMeses().size());
        assertTrue(tempo.getMeses().get(1).melhor());
        assertEquals("Outubro", tempo.getMeses().get(1).nome());
        assertFalse(tempo.getMeses().get(0).melhor());
        assertThrows(UnsupportedOperationException.class, () -> tempo.getMeses().clear());
    }

    @Test
    void tempoValidaEstacaoEMeses() {
        assertThrows(IllegalArgumentException.class, () -> new Tempo(null, "", 10, ""));
        assertThrows(IllegalArgumentException.class, () -> new Tempo(Estacao.PRIMAVERA, "", 3, ""));
        Tempo tempo = new Tempo(Estacao.INVERNO, null, 8, null);
        assertEquals("", tempo.getDescricao());
        assertThrows(IllegalArgumentException.class, () -> tempo.adicionarMes(1, ""));
    }

    // ---------------- Saúde ----------------

    @Test
    void itemSaudeValidaCampos() {
        assertThrows(IllegalArgumentException.class, () -> new ItemSaude(null, "Cebola", "x", Gravidade.GRAVE));
        assertThrows(IllegalArgumentException.class, () -> new ItemSaude("id", " ", "x", Gravidade.GRAVE));
        assertThrows(IllegalArgumentException.class, () -> new ItemSaude("id", "Cebola", null, Gravidade.GRAVE));
        assertNull(new ItemSaude("id", "Cebola", " motivo ", null).gravidade());
        assertEquals("motivo", new ItemSaude("id", "Cebola", " motivo ", null).motivo());
    }

    @Test
    void registroSaudeGuardaListasDeSubdocumentos() {
        RegistroSaude r = new RegistroSaude(" gatos ", TipoRegistroSaude.ANIMAL, " Gatos ", null, null, null)
                .adicionarEvitar(new ItemSaude("p1", "Cebola", "anemia", Gravidade.GRAVE))
                .adicionarRecomendada(new ItemSaude("p2", "Manjericão", "seguro", null));
        r.setId("r1");

        assertEquals("r1", r.getId());
        assertEquals("GATOS", r.getCodigo());
        assertEquals("Gatos", r.getNome());
        assertEquals("", r.getDescricao());
        assertEquals("", r.getOrientacao());
        assertEquals("", r.getAviso());
        assertEquals(TipoRegistroSaude.ANIMAL, r.getTipo());
        assertEquals(1, r.getEvitar().size());
        assertEquals(1, r.getRecomendadas().size());
        assertThrows(UnsupportedOperationException.class, () -> r.getEvitar().clear());
        assertThrows(UnsupportedOperationException.class, () -> r.getRecomendadas().clear());
    }

    @Test
    void registroSaudeValidaCamposEGravidadeDoEvitar() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroSaude(" ", TipoRegistroSaude.ANIMAL, "x", "", "", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroSaude("X", null, "x", "", "", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroSaude("X", TipoRegistroSaude.ANIMAL, " ", "", "", ""));
        RegistroSaude r = new RegistroSaude("X", TipoRegistroSaude.COMORBIDADE, "x", "", "", "");
        assertThrows(IllegalArgumentException.class,
                () -> r.adicionarEvitar(new ItemSaude("p", "Planta", "motivo", null)));
    }
}
