package com.ecohorta.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JanelaPlantioTest {

    @Test
    void janelaNormalContemApenasOsMesesDoIntervalo() {
        JanelaPlantio janela = new JanelaPlantio(9, 11, "Primavera");
        assertTrue(janela.contem(9));
        assertTrue(janela.contem(10));
        assertTrue(janela.contem(11));
        assertFalse(janela.contem(8));
        assertFalse(janela.contem(12));
    }

    @Test
    void janelaQueCruzaOAnoContemFimEInicioDoAno() {
        JanelaPlantio janela = new JanelaPlantio(11, 2, "Verão");
        assertTrue(janela.contem(11));
        assertTrue(janela.contem(12));
        assertTrue(janela.contem(1));
        assertTrue(janela.contem(2));
        assertFalse(janela.contem(3));
        assertFalse(janela.contem(10));
    }

    @Test
    void contemAceitaLocalDateEIgnoraMesesInvalidos() {
        JanelaPlantio janela = new JanelaPlantio(3, 8, "");
        assertTrue(janela.contem(LocalDate.of(2026, 5, 20)));
        assertFalse(janela.contem(LocalDate.of(2026, 11, 10)));
        assertFalse(janela.contem(0));
        assertFalse(janela.contem(13));
    }

    @Test
    void mesesForaDeIntervaloSaoRejeitados() {
        assertThrows(IllegalArgumentException.class, () -> new JanelaPlantio(0, 5, ""));
        assertThrows(IllegalArgumentException.class, () -> new JanelaPlantio(5, 13, ""));
    }

    @Test
    void descricaoFormataPeriodoEObservacao() {
        assertEquals("Ano todo", new JanelaPlantio(1, 12, null).descricao());
        assertEquals("Mar", new JanelaPlantio(3, 3, "").descricao());
        assertEquals("Set a Nov (Primavera)", new JanelaPlantio(9, 11, " Primavera ").descricao());
    }

    @Test
    void nomeDoMesValidaOIntervalo() {
        assertEquals("Jan", JanelaPlantio.nomeDoMes(1));
        assertEquals("Dez", JanelaPlantio.nomeDoMes(12));
        assertThrows(IllegalArgumentException.class, () -> JanelaPlantio.nomeDoMes(13));
    }
}
