package com.ecohorta.service;

import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.Cuidado;
import com.ecohorta.model.TipoCuidado;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CuidadosPadraoTest {

    @Test
    void categoriaOutraRecebeAdubacaoPeriodicaGenerica() {
        List<Cuidado> cuidados = CuidadosPadrao.para(CategoriaPlanta.OUTRA);
        assertEquals(1, cuidados.size());
        assertEquals(TipoCuidado.ADUBACAO, cuidados.get(0).tipo());
        assertEquals(30, cuidados.get(0).frequenciaDias());
        assertTrue(cuidados.get(0).periodico());
        assertFalse(cuidados.get(0).descricao().isBlank());
    }
}
