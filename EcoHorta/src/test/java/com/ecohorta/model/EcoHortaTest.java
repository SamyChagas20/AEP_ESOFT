package com.ecohorta.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EcoHortaTest {

    private Planta planta;

    @BeforeEach
    void setUp() {
        // Inicializa uma planta para testes com rega a cada 2 dias
        planta = new Planta("Tomate", "Outono/Primavera", 2, 90, "Sol pleno", null);
    }

    @Test
    @DisplayName("1. Deve precisar de rega quando nunca foi regada")
    void testPrecisaRegarQuandoNuncaRegada() {
        assertNull(planta.getUltimaRega());
        assertTrue(planta.precisaRegarHoje(), "Planta sem rega anterior deve precisar regar hoje.");
        assertEquals(LocalDate.now(), planta.calcularProximaRega());
    }

    @Test
    @DisplayName("2. Deve calcular a próxima rega corretamente baseada nos dias")
    void testCalcularProximaRega() {
        LocalDate hoje = LocalDate.now();
        planta.setUltimaRega(hoje);

        LocalDate proximaRegaEsperada = hoje.plusDays(2);
        assertEquals(proximaRegaEsperada, planta.calcularProximaRega());
    }

    @Test
    @DisplayName("3. Não deve precisar regar se foi regada hoje")
    void testNaoPrecisaRegarHoje() {
        planta.setUltimaRega(LocalDate.now());
        assertFalse(planta.precisaRegarHoje(), "Se regou hoje, não deve precisar regar novamente.");
    }

    @Test
    @DisplayName("4. Deve precisar regar se o prazo limite chegou ou venceu")
    void testPrecisaRegarPrazoVencido() {
        // Regada há exatamente 2 dias (frequência = 2)
        planta.setUltimaRega(LocalDate.now().minusDays(2));
        assertTrue(planta.precisaRegarHoje());

        // Regada há 3 dias (prazo estourado)
        planta.setUltimaRega(LocalDate.now().minusDays(3));
        assertTrue(planta.precisaRegarHoje());
    }

    @Test
    @DisplayName("5. Deve formatar o toString corretamente com e sem rega")
    void testToStringFormatacao() {
        assertTrue(planta.toString().contains("nunca regada"));

        planta.setUltimaRega(LocalDate.of(2026, 4, 15));
        assertTrue(planta.toString().contains("15/04/2026"));
    }

    @Test
    @DisplayName("6. Teste de Getters e Setters básicos")
    void testGettersESetters() {
        planta.setNome("Cebolinha");
        planta.setFrequenciaRegaDias(1);
        
        assertEquals("Cebolinha", planta.getNome());
        assertEquals(1, planta.getFrequenciaRegaDias());
    }
}