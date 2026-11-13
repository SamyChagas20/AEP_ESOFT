package com.ecohorta.service;

import com.ecohorta.model.Planta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogoInicialTest {

    @Test
    void asCincoPlantasDetalhadasVemPrimeiro() {
        List<String> nomes = CatalogoInicial.detalhadas().stream().map(Planta::getNome).toList();
        assertEquals(List.of("Tomate", "Cebolinha", "Pimenta", "Alface", "Salsinha"), nomes);
        List<String> primeiras = CatalogoInicial.plantas().stream().limit(5).map(Planta::getNome).toList();
        assertEquals(nomes, primeiras);
    }

    @Test
    void catalogoCompletoTemMaisDe200PlantasAlemDasCinco() {
        assertTrue(CatalogoInicial.plantas().size() >= 205, "esperado pelo menos 205 plantas");
    }

    @Test
    void detalhadasTemRegaJanelasCuidadosELuzCadastrados() {
        for (Planta planta : CatalogoInicial.detalhadas()) {
            assertTrue(planta.getRega().frequenciaDias() >= 1, planta.getNome() + ": rega");
            assertFalse(planta.getJanelasPlantio().isEmpty(), planta.getNome() + ": janelas");
            assertFalse(planta.getCuidados().isEmpty(), planta.getNome() + ": cuidados");
            assertFalse(planta.getNomeCientifico().isEmpty(), planta.getNome() + ": nome científico");
            assertFalse(planta.getApartamento().observacao().isEmpty(), planta.getNome() + ": apartamento");
        }
    }

    @Test
    void epocasDePlantioEstaoCoerentes() {
        Planta tomate = CatalogoInicial.tomate();
        assertTrue(tomate.epocaAdequada(9));
        assertFalse(tomate.epocaAdequada(6));

        assertTrue(CatalogoInicial.cebolinha().epocaAdequada(1));
        assertTrue(CatalogoInicial.cebolinha().epocaAdequada(12));

        // janela de salsinha cruza o ano (nov a fev)
        assertTrue(CatalogoInicial.salsinha().epocaAdequada(12));
        assertTrue(CatalogoInicial.salsinha().epocaAdequada(1));
        assertTrue(CatalogoInicial.pimenta().epocaAdequada(10));
        assertFalse(CatalogoInicial.pimenta().epocaAdequada(5));
        assertTrue(CatalogoInicial.alface().epocaAdequada(7));
    }
}
