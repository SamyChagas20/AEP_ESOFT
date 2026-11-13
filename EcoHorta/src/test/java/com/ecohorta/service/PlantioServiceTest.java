package com.ecohorta.service;

import com.ecohorta.model.Horta;
import com.ecohorta.model.Plantio;
import com.ecohorta.model.StatusPlantio;
import com.ecohorta.model.Tarefa;
import com.ecohorta.model.TipoCuidado;
import com.ecohorta.model.TipoTarefa;
import com.ecohorta.model.Usuario;
import com.ecohorta.support.Ambiente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.ecohorta.support.Ambiente.HOJE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantioServiceTest {

    private final Ambiente ambiente = new Ambiente();
    private final PlantioService service = ambiente.servicos.plantios();
    private Horta horta;

    @BeforeEach
    void preparar() {
        ambiente.servicos.catalogo().carregarCatalogoInicial();
        Usuario usuario = ambiente.servicos.usuarios().cadastrar("Ana", "ana@exemplo.com");
        horta = ambiente.servicos.hortas().criarHorta(usuario.getId(), "Quintal", "Maringá");
        horta = ambiente.servicos.hortas().adicionarCanteiro(horta.getId(), "C1", 2.0, "Sol pleno");
    }

    private Plantio plantar(String planta, int diasAtras) {
        return service.iniciarPlantio(horta.getId(), planta, "C1", 3, HOJE.minusDays(diasAtras));
    }

    // ---------------- iniciar plantio ----------------

    @Test
    void iniciarPlantioLigaHortaEPlantaPorReferencia() {
        Plantio plantio = plantar("tomate", 2);

        assertNotNull(plantio.getId());
        assertEquals(horta.getId(), plantio.getHortaId());
        assertEquals(ambiente.plantas.buscarPorNome("Tomate").orElseThrow().getId(), plantio.getPlantaId());
        assertEquals("C1", plantio.getCanteiro());
        assertEquals(1, service.listarPorHorta(horta.getId()).size());
    }

    @Test
    void iniciarPlantioRejeitaReferenciasInexistentes() {
        LocalDate dia = HOJE.minusDays(1);
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.iniciarPlantio("000000000000000000000000", "Tomate", "C1", 1, dia));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.iniciarPlantio(horta.getId(), "Planta Inexistente", "C1", 1, dia));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.iniciarPlantio(horta.getId(), "Tomate", "Inexistente", 1, dia));
    }

    @Test
    void iniciarPlantioRejeitaDataFutura() {
        assertThrows(RegraDeNegocioException.class,
                () -> service.iniciarPlantio(horta.getId(), "Tomate", "C1", 1, HOJE.plusDays(1)));
    }

    // ---------------- regas e cuidados ----------------

    @Test
    void registrarRegaUsaQuantidadeEDataPadraoQuandoOmitidas() {
        Plantio plantio = plantar("Tomate", 2);

        Plantio atualizado = service.registrarRega(plantio.getId(), null, "", null);

        assertEquals(1000, atualizado.getRegas().get(0).quantidadeMl());
        assertEquals(HOJE, atualizado.getRegas().get(0).data());
        // foi persistido, não apenas alterado em memória
        assertEquals(1, ambiente.plantios.buscarPorId(plantio.getId()).orElseThrow().getRegas().size());
    }

    @Test
    void registrarRegaAceitaQuantidadeEDataInformadas() {
        Plantio plantio = plantar("Tomate", 5);
        Plantio atualizado = service.registrarRega(plantio.getId(), 700, "tarde", HOJE.minusDays(1));
        assertEquals(700, atualizado.getRegas().get(0).quantidadeMl());
        assertEquals(HOJE.minusDays(1), atualizado.ultimaRega().orElseThrow());
    }

    @Test
    void registrarRegaRejeitaDataFuturaPlantioInexistenteEPlantioEncerrado() {
        Plantio plantio = plantar("Tomate", 5);
        String id = plantio.getId();
        assertThrows(RegraDeNegocioException.class, () -> service.registrarRega(id, 100, "", HOJE.plusDays(1)));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.registrarRega("nao-existe", 100, "", null));

        service.colher(id, null);
        assertThrows(IllegalStateException.class, () -> service.registrarRega(id, 100, "", null));
    }

    @Test
    void registrarCuidadoPersisteHistorico() {
        Plantio plantio = plantar("Tomate", 10);

        Plantio atualizado = service.registrarCuidado(plantio.getId(), TipoCuidado.ADUBACAO, "NPK", null);

        assertEquals(HOJE, atualizado.ultimoCuidado(TipoCuidado.ADUBACAO).orElseThrow());
        assertEquals(1, ambiente.plantios.buscarPorId(plantio.getId()).orElseThrow().getCuidados().size());
        String id = plantio.getId();
        assertThrows(RegraDeNegocioException.class,
                () -> service.registrarCuidado(id, TipoCuidado.PODA, "", HOJE.plusDays(2)));
    }

    // ---------------- encerramento e resumo ----------------

    @Test
    void colherEMarcarComoPerdidoAtualizamOResumo() {
        Plantio colhido = plantar("Alface", 40);
        Plantio perdido = plantar("Salsinha", 10);
        plantar("Tomate", 3);

        service.colher(colhido.getId(), HOJE);
        service.marcarComoPerdido(perdido.getId(), null);

        Map<StatusPlantio, Long> resumo = service.resumo(horta.getId());
        assertEquals(1L, resumo.get(StatusPlantio.ATIVO));
        assertEquals(1L, resumo.get(StatusPlantio.COLHIDO));
        assertEquals(1L, resumo.get(StatusPlantio.PERDIDO));
        assertEquals(HOJE, ambiente.plantios.buscarPorId(perdido.getId()).orElseThrow().getDataEncerramento());
    }

    @Test
    void buscarPlantaInexistenteLancaExcecao() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPlanta("000000000000000000000000"));
    }

    // ---------------- tarefas do dia ----------------

    @Test
    void plantioSemHistoricoGeraRegaECuidadosAtrasados() {
        plantar("Tomate", 20); // plantado em 21/10

        List<Tarefa> tarefas = service.tarefasDoDia(horta.getId(), HOJE);

        assertEquals(5, tarefas.size());
        assertEquals(TipoTarefa.REGA, tarefas.get(0).tipo());
        assertTrue(tarefas.get(0).atrasada());
        assertEquals("C1", tarefas.get(0).canteiro());
        assertEquals("Tomate", tarefas.get(0).plantaNome());
        assertTrue(tarefas.get(4).descricao().startsWith("Adubação"));
        assertTrue(tarefas.stream().allMatch(Tarefa::atrasada));
    }

    @Test
    void registrosFeitosHojeRemovemAsTarefasCorrespondentes() {
        Plantio plantio = plantar("Tomate", 20);
        service.registrarRega(plantio.getId(), null, "", null);
        service.registrarCuidado(plantio.getId(), TipoCuidado.ADUBACAO, "NPK", null);

        List<Tarefa> tarefas = service.tarefasDoDia(horta.getId(), HOJE);

        assertEquals(3, tarefas.size()); // sustentação, poda e pragas continuam pendentes
        assertTrue(tarefas.stream().allMatch(t -> t.tipo() == TipoTarefa.CUIDADO));
    }

    @Test
    void tarefaDevidaHojeNaoEstaAtrasada() {
        Plantio plantio = plantar("Cebolinha", 65);
        service.registrarRega(plantio.getId(), null, "", HOJE.minusDays(1));

        List<Tarefa> tarefas = service.tarefasDoDia(horta.getId(), HOJE);

        // ordenadas por data prevista: adubação (06/10), colheita (05/11) e rega (hoje)
        assertEquals(List.of(TipoTarefa.CUIDADO, TipoTarefa.COLHEITA, TipoTarefa.REGA),
                tarefas.stream().map(Tarefa::tipo).toList());
        assertEquals(List.of(true, true, false), tarefas.stream().map(Tarefa::atrasada).toList());
    }

    @Test
    void plantioEmDiaNaoGeraTarefas() {
        Plantio plantio = plantar("Alface", 0);
        service.registrarRega(plantio.getId(), null, "", null);
        assertTrue(service.tarefasDoDia(horta.getId(), HOJE).isEmpty());
    }

    @Test
    void plantiosEncerradosNaoGeramTarefas() {
        Plantio plantio = plantar("Tomate", 20);
        assertFalse(service.tarefasDoDia(horta.getId(), HOJE).isEmpty());

        service.colher(plantio.getId(), HOJE);

        assertTrue(service.tarefasDoDia(horta.getId(), HOJE).isEmpty());
    }

    @Test
    void tarefasDoDiaExigemHortaExistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.tarefasDoDia("000000000000000000000000", HOJE));
    }
}
