package com.ecohorta.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantaTest {

    private Planta alface() {
        return new Planta("Alface", "Lactuca sativa", 45, "Meia sombra",
                new RegaRecomendada(1, 250, "Regas leves"))
                .adicionarJanela(3, 8, "Melhor época")
                .adicionarJanela(9, 2, "Verão")
                .adicionarCuidado(TipoCuidado.ADUBACAO, "Adubar", 15)
                .adicionarCuidado(TipoCuidado.SOLO, "Solo rico", 0);
    }

    @Test
    void guardaDadosBasicosEListasAninhadas() {
        Planta planta = alface();
        planta.setId("p1");
        assertEquals("p1", planta.getId());
        assertEquals("Alface", planta.getNome());
        assertEquals("Lactuca sativa", planta.getNomeCientifico());
        assertEquals(45, planta.getDiasParaColheita());
        assertEquals("Meia sombra", planta.getLuminosidade());
        assertEquals(1, planta.getRega().frequenciaDias());
        assertEquals(2, planta.getJanelasPlantio().size());
        assertEquals(2, planta.getCuidados().size());
    }

    @Test
    void validaCamposObrigatorios() {
        RegaRecomendada rega = new RegaRecomendada(1, 100, "");
        assertThrows(IllegalArgumentException.class, () -> new Planta(" ", "", 10, "", rega));
        assertThrows(IllegalArgumentException.class, () -> new Planta(null, "", 10, "", rega));
        assertThrows(IllegalArgumentException.class, () -> new Planta("X", "", 0, "", rega));
        assertThrows(IllegalArgumentException.class, () -> new Planta("X", "", 10, "", null));
    }

    @Test
    void camposOpcionaisNulosViramTextoVazio() {
        Planta planta = new Planta("X", null, 10, null, new RegaRecomendada(1, 100, ""));
        assertEquals("", planta.getNomeCientifico());
        assertEquals("", planta.getLuminosidade());
    }

    @Test
    void epocaAdequadaConsideraTodasAsJanelas() {
        Planta planta = alface();
        assertTrue(planta.epocaAdequada(5));
        assertTrue(planta.epocaAdequada(12));
        assertTrue(planta.epocaAdequada(LocalDate.of(2026, 11, 10)));

        Planta tomate = new Planta("Tomate", "", 90, "", new RegaRecomendada(2, 1000, ""))
                .adicionarJanela(8, 10, "");
        assertFalse(tomate.epocaAdequada(LocalDate.of(2026, 11, 10)));
    }

    @Test
    void janelaParaDevolveAPrimeiraJanelaQueContemOMes() {
        Planta planta = alface();
        assertEquals("Melhor época", planta.janelaPara(4).orElseThrow().observacao());
        assertEquals("Verão", planta.janelaPara(1).orElseThrow().observacao());

        Planta tomate = new Planta("Tomate", "", 90, "", new RegaRecomendada(2, 1000, ""))
                .adicionarJanela(8, 10, "");
        assertTrue(tomate.janelaPara(1).isEmpty());
    }

    @Test
    void cuidadosPeriodicosIgnoramOrientacoes() {
        Planta planta = alface();
        assertEquals(1, planta.cuidadosPeriodicos().size());
        assertEquals(TipoCuidado.ADUBACAO, planta.cuidadosPeriodicos().get(0).tipo());
    }

    @Test
    void listasNaoPodemSerAlteradasDeFora() {
        Planta planta = alface();
        assertThrows(UnsupportedOperationException.class, () -> planta.getJanelasPlantio().clear());
        assertThrows(UnsupportedOperationException.class, () -> planta.getCuidados().clear());
    }

    @Test
    void naoAceitaJanelaOuCuidadoNulos() {
        Planta planta = alface();
        assertThrows(NullPointerException.class, () -> planta.adicionarJanela((JanelaPlantio) null));
        assertThrows(NullPointerException.class, () -> planta.adicionarCuidado((Cuidado) null));
    }

    @Test
    void valoresPadraoDosAtributosNovos() {
        Planta planta = new Planta("X", "", 10, "", new RegaRecomendada(1, 100, ""));
        assertEquals(CategoriaPlanta.OUTRA, planta.getCategoria());
        assertEquals(NivelLuz.PLENA, planta.getNivelLuz());
        assertEquals(6, planta.getHorasSolMinimas());
        assertEquals(NivelApartamento.NAO_RECOMENDADO, planta.getApartamento().nivel());
        assertTrue(planta.isComestivel());
    }

    @Test
    void montagemFluenteDosAtributosNovos() {
        Planta planta = new Planta("Jiboia", "", 365, "", new RegaRecomendada(7, 200, ""))
                .categoria(CategoriaPlanta.ORNAMENTAL)
                .nivelLuz(NivelLuz.MINIMA)
                .apartamento(new AdaptacaoApartamento(NivelApartamento.IDEAL, "Janela"))
                .comestivel(false);
        assertEquals(CategoriaPlanta.ORNAMENTAL, planta.getCategoria());
        assertEquals(NivelLuz.MINIMA, planta.getNivelLuz());
        assertEquals(2, planta.getHorasSolMinimas());
        assertEquals("Janela", planta.getApartamento().observacao());
        assertFalse(planta.isComestivel());
        assertThrows(NullPointerException.class, () -> planta.categoria(null));
        assertThrows(NullPointerException.class, () -> planta.nivelLuz(null));
        assertThrows(NullPointerException.class, () -> planta.apartamento(null));
    }

    @Test
    void estacoesSaoDerivadasDasJanelasDePlantio() {
        assertEquals(List.of(Estacao.PRIMAVERA, Estacao.VERAO, Estacao.OUTONO, Estacao.INVERNO),
                alface().getEstacoes());

        Planta tomate = new Planta("Tomate", "", 90, "", new RegaRecomendada(2, 1000, ""))
                .adicionarJanela(8, 10, "");
        assertEquals(List.of(Estacao.PRIMAVERA, Estacao.INVERNO), tomate.getEstacoes());

        assertTrue(new Planta("X", "", 10, "", new RegaRecomendada(1, 100, "")).getEstacoes().isEmpty());
    }

    @Test
    void melhorMesEoMesCentralDaPrimeiraJanela() {
        Planta primavera = new Planta("A", "", 10, "", new RegaRecomendada(1, 100, "")).adicionarJanela(9, 11, "");
        assertEquals(10, primavera.getMelhorMesPlantio().getAsInt());

        Planta cruzaAno = new Planta("B", "", 10, "", new RegaRecomendada(1, 100, "")).adicionarJanela(11, 2, "");
        assertEquals(12, cruzaAno.getMelhorMesPlantio().getAsInt());

        Planta longa = new Planta("C", "", 10, "", new RegaRecomendada(1, 100, "")).adicionarJanela(3, 8, "");
        assertEquals(5, longa.getMelhorMesPlantio().getAsInt());

        Planta anoTodo = new Planta("D", "", 10, "", new RegaRecomendada(1, 100, "")).adicionarJanela(1, 12, "");
        assertTrue(anoTodo.getMelhorMesPlantio().isEmpty());

        assertTrue(new Planta("E", "", 10, "", new RegaRecomendada(1, 100, "")).getMelhorMesPlantio().isEmpty());
    }

    @Test
    void nomeDeBuscaIgnoraAcentosEMaiusculas() {
        Planta planta = new Planta("Açafrão", "", 10, "", new RegaRecomendada(1, 100, ""));
        assertEquals("acafrao", planta.getNomeBusca());
    }
}
