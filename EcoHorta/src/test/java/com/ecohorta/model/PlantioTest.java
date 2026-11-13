package com.ecohorta.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantioTest {

    private static final LocalDate PLANTIO = LocalDate.of(2026, 10, 1);

    private Planta tomate;
    private Plantio plantio;

    @BeforeEach
    void preparar() {
        tomate = new Planta("Tomate", "Solanum lycopersicum", 90, "Sol pleno",
                new RegaRecomendada(2, 1000, ""))
                .adicionarCuidado(TipoCuidado.ADUBACAO, "Adubar", 15)
                .adicionarCuidado(TipoCuidado.SOLO, "Solo rico", 0);
        plantio = new Plantio("h1", "p1", " Canteiro A ", 3, PLANTIO);
    }

    @Test
    void novoPlantioComecaAtivoESemHistorico() {
        assertTrue(plantio.isAtivo());
        assertEquals(StatusPlantio.ATIVO, plantio.getStatus());
        assertEquals("Canteiro A", plantio.getCanteiro());
        assertEquals("h1", plantio.getHortaId());
        assertEquals("p1", plantio.getPlantaId());
        assertEquals(3, plantio.getQuantidade());
        assertEquals(PLANTIO, plantio.getDataPlantio());
        assertNull(plantio.getDataEncerramento());
        assertNull(plantio.getId());
        assertTrue(plantio.getRegas().isEmpty());
        assertTrue(plantio.getCuidados().isEmpty());
        plantio.setId("x1");
        assertEquals("x1", plantio.getId());
    }

    @Test
    void validaDadosObrigatorios() {
        assertThrows(IllegalArgumentException.class, () -> new Plantio(null, "p", "c", 1, PLANTIO));
        assertThrows(IllegalArgumentException.class, () -> new Plantio("h", " ", "c", 1, PLANTIO));
        assertThrows(IllegalArgumentException.class, () -> new Plantio("h", "p", " ", 1, PLANTIO));
        assertThrows(IllegalArgumentException.class, () -> new Plantio("h", "p", "c", 0, PLANTIO));
        assertThrows(NullPointerException.class, () -> new Plantio("h", "p", "c", 1, null));
    }

    @Test
    void semRegaAPrimeiraRegaEDevidaNoDiaDoPlantio() {
        assertTrue(plantio.ultimaRega().isEmpty());
        assertEquals(PLANTIO, plantio.proximaRega(tomate));
    }

    @Test
    void proximaRegaSomaAFrequenciaAUltimaRega() {
        plantio.registrarRega(LocalDate.of(2026, 10, 5), 800, "manhã");
        plantio.registrarRega(LocalDate.of(2026, 10, 3), 800, "registro fora de ordem");

        assertEquals(LocalDate.of(2026, 10, 5), plantio.ultimaRega().orElseThrow());
        assertEquals(LocalDate.of(2026, 10, 7), plantio.proximaRega(tomate));
        assertEquals(2, plantio.getRegas().size());
    }

    @Test
    void proximoCuidadoUsaOPlantioOuOUltimoCuidadoDoMesmoTipo() {
        Cuidado adubacao = tomate.getCuidados().get(0);
        assertEquals(LocalDate.of(2026, 10, 16), plantio.proximoCuidado(adubacao));
        assertTrue(plantio.ultimoCuidado(TipoCuidado.ADUBACAO).isEmpty());

        plantio.registrarCuidado(LocalDate.of(2026, 10, 20), TipoCuidado.ADUBACAO, "NPK");
        plantio.registrarCuidado(LocalDate.of(2026, 10, 10), TipoCuidado.PODA, "Desbrota");

        assertEquals(LocalDate.of(2026, 10, 20), plantio.ultimoCuidado(TipoCuidado.ADUBACAO).orElseThrow());
        assertEquals(LocalDate.of(2026, 11, 4), plantio.proximoCuidado(adubacao));
    }

    @Test
    void calculaColheitaEDiasDesdeOPlantio() {
        assertEquals(LocalDate.of(2026, 12, 30), plantio.dataPrevistaColheita(tomate));
        assertEquals(40, plantio.diasDesdePlantio(LocalDate.of(2026, 11, 10)));
    }

    @Test
    void naoPermiteRegaOuCuidadoAntesDoPlantio() {
        LocalDate antes = PLANTIO.minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> plantio.registrarRega(antes, 100, ""));
        assertThrows(IllegalArgumentException.class,
                () -> plantio.registrarCuidado(antes, TipoCuidado.PODA, ""));
        assertThrows(IllegalArgumentException.class, () -> plantio.registrarRega(null, 100, ""));
    }

    @Test
    void colherEncerraOPlantio() {
        plantio.colher(LocalDate.of(2026, 12, 20));
        assertEquals(StatusPlantio.COLHIDO, plantio.getStatus());
        assertFalse(plantio.isAtivo());
        assertEquals(LocalDate.of(2026, 12, 20), plantio.getDataEncerramento());
    }

    @Test
    void marcarComoPerdidoEncerraOPlantio() {
        plantio.marcarComoPerdido(LocalDate.of(2026, 10, 15));
        assertEquals(StatusPlantio.PERDIDO, plantio.getStatus());
    }

    @Test
    void plantioEncerradoNaoAceitaNovosRegistros() {
        plantio.colher(LocalDate.of(2026, 12, 20));
        LocalDate dia = LocalDate.of(2026, 12, 21);
        assertThrows(IllegalStateException.class, () -> plantio.registrarRega(dia, 100, ""));
        assertThrows(IllegalStateException.class, () -> plantio.registrarCuidado(dia, TipoCuidado.PODA, ""));
        assertThrows(IllegalStateException.class, () -> plantio.colher(dia));
        assertThrows(IllegalStateException.class, () -> plantio.marcarComoPerdido(dia));
    }

    @Test
    void naoPermiteEncerrarAntesDoPlantio() {
        assertThrows(IllegalArgumentException.class, () -> plantio.colher(PLANTIO.minusDays(1)));
    }

    @Test
    void restaurarReconstroiOEstadoSalvo() {
        Plantio restaurado = Plantio.restaurar("id1", "h1", "p1", "C", 2, PLANTIO,
                StatusPlantio.COLHIDO, LocalDate.of(2026, 12, 1),
                List.of(new RegistroRega(PLANTIO, 100, "")),
                List.of(new RegistroCuidado(PLANTIO, TipoCuidado.PODA, "")));

        assertEquals("id1", restaurado.getId());
        assertEquals(StatusPlantio.COLHIDO, restaurado.getStatus());
        assertEquals(LocalDate.of(2026, 12, 1), restaurado.getDataEncerramento());
        assertEquals(1, restaurado.getRegas().size());
        assertEquals(1, restaurado.getCuidados().size());
    }

    @Test
    void restaurarSemStatusAssumeAtivo() {
        Plantio restaurado = Plantio.restaurar("id1", "h1", "p1", "C", 2, PLANTIO,
                null, null, List.of(), List.of());
        assertEquals(StatusPlantio.ATIVO, restaurado.getStatus());
    }
}
