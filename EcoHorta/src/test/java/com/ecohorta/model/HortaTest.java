package com.ecohorta.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HortaTest {

    @Test
    void criaHortaComValoresPadrao() {
        Horta horta = new Horta("u1", " Quintal ", " ");
        assertEquals("Quintal", horta.getNome());
        assertEquals("Não informada", horta.getLocalizacao());
        assertEquals("u1", horta.getUsuarioId());
        assertTrue(horta.getCanteiros().isEmpty());
        horta.setId("h1");
        assertEquals("h1", horta.getId());
    }

    @Test
    void exigeUsuarioENome() {
        assertThrows(IllegalArgumentException.class, () -> new Horta(null, "Quintal", ""));
        assertThrows(IllegalArgumentException.class, () -> new Horta(" ", "Quintal", ""));
        assertThrows(IllegalArgumentException.class, () -> new Horta("u1", " ", ""));
    }

    @Test
    void adicionaEBuscaCanteirosIgnorandoMaiusculas() {
        Horta horta = new Horta("u1", "Quintal", "Maringá");
        horta.adicionarCanteiro(new Canteiro("Canteiro Sol", 4, "Sol pleno"));

        assertEquals(1, horta.getCanteiros().size());
        assertTrue(horta.buscarCanteiro("canteiro sol").isPresent());
        assertFalse(horta.buscarCanteiro("outro").isPresent());
        assertFalse(horta.buscarCanteiro(null).isPresent());
    }

    @Test
    void naoPermiteCanteirosComNomeRepetido() {
        Horta horta = new Horta("u1", "Quintal", "");
        horta.adicionarCanteiro(new Canteiro("A", 1, ""));
        assertThrows(IllegalArgumentException.class, () -> horta.adicionarCanteiro(new Canteiro("a", 2, "")));
    }

    @Test
    void listaDeCanteirosNaoPodeSerAlteradaDeFora() {
        Horta horta = new Horta("u1", "Quintal", "");
        assertThrows(UnsupportedOperationException.class,
                () -> horta.getCanteiros().add(new Canteiro("A", 1, "")));
    }
}
