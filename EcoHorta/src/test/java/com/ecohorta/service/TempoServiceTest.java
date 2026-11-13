package com.ecohorta.service;

import com.ecohorta.model.Estacao;
import com.ecohorta.model.MesDaEstacao;
import com.ecohorta.model.Planta;
import com.ecohorta.model.Tempo;
import com.ecohorta.support.Ambiente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempoServiceTest {

    private final Ambiente ambiente = new Ambiente();
    private final TempoService service = ambiente.servicos.tempo();

    @Test
    void carregaAsQuatroEstacoesSemDuplicar() {
        assertEquals(4, service.carregarTempoInicial());
        assertEquals(0, service.carregarTempoInicial());
        assertEquals(4, service.listar().size());
    }

    @Test
    void listaNaOrdemPrimaveraVeraoOutonoInverno() {
        service.carregarTempoInicial();
        assertEquals(List.of(Estacao.PRIMAVERA, Estacao.VERAO, Estacao.OUTONO, Estacao.INVERNO),
                service.listar().stream().map(Tempo::getEstacao).toList());
    }

    @Test
    void cadaEstacaoTemUmMelhorMesDentroDela() {
        service.carregarTempoInicial();
        assertEquals(10, service.buscar(Estacao.PRIMAVERA).getMelhorMes());
        assertEquals(12, service.buscar(Estacao.VERAO).getMelhorMes());
        assertEquals(4, service.buscar(Estacao.OUTONO).getMelhorMes());
        assertEquals(8, service.buscar(Estacao.INVERNO).getMelhorMes());
        for (Tempo t : service.listar()) {
            assertEquals(3, t.getMeses().size(), t.getEstacao().name());
            assertEquals(1L, t.getMeses().stream().filter(MesDaEstacao::melhor).count());
            assertFalse(t.getJustificativaMelhorMes().isEmpty());
            assertTrue(t.getMeses().stream().allMatch(m -> t.getEstacao().contem(m.numero())));
        }
    }

    @Test
    void buscarEstacaoNaoCarregadaLancaExcecao() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscar(Estacao.VERAO));
    }

    @Test
    void descobreAEstacaoDeUmaData() {
        assertEquals(Estacao.PRIMAVERA, service.estacaoDe(LocalDate.of(2026, 11, 10)));
        assertEquals(Estacao.VERAO, service.estacaoDe(LocalDate.of(2027, 1, 5)));
    }

    @Test
    void listaPlantasDaEstacaoEDoMelhorMes() {
        ambiente.servicos.carregarDadosIniciais();

        List<String> primavera = service.plantasDaEstacao(Estacao.PRIMAVERA).stream().map(Planta::getNome).toList();
        assertTrue(primavera.contains("Tomate"));
        assertTrue(primavera.contains("Pimenta"));
        assertFalse(primavera.contains("Alho")); // alho: abril a junho

        List<Planta> outubro = service.plantasDoMelhorMes(Estacao.PRIMAVERA);
        assertFalse(outubro.isEmpty());
        assertTrue(outubro.stream().allMatch(p -> p.epocaAdequada(10)));
    }

    @Test
    void contaPlantasPorMesDaEstacao() {
        ambiente.servicos.carregarDadosIniciais();

        Map<Integer, Long> contagem = service.plantasPorMes(Estacao.VERAO);

        assertEquals(List.of(12, 1, 2), List.copyOf(contagem.keySet()));
        assertTrue(contagem.values().stream().allMatch(n -> n > 0));
        long emDezembro = ambiente.servicos.catalogo().plantasParaPlantar(12).size();
        assertEquals(emDezembro, contagem.get(12));
    }
}
