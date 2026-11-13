package com.ecohorta.demo;

import com.ecohorta.model.Horta;
import com.ecohorta.model.Tarefa;
import com.ecohorta.model.TipoTarefa;
import com.ecohorta.model.Usuario;
import com.ecohorta.service.CatalogoInicial;
import com.ecohorta.support.Ambiente;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ecohorta.support.Ambiente.HOJE;
import static com.ecohorta.support.Ambiente.RELOGIO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoDataLoaderTest {

    private final Ambiente ambiente = new Ambiente();
    private final DemoDataLoader loader = new DemoDataLoader(ambiente.servicos, RELOGIO);

    @Test
    void criaUsuarioHortaCanteirosEPlantiosDeDemonstracao() {
        Usuario usuario = loader.carregar();

        assertEquals(DemoDataLoader.EMAIL_DEMO, usuario.getEmail());
        assertEquals((long) CatalogoInicial.plantas().size(), ambiente.plantas.contar());

        List<Horta> hortas = ambiente.servicos.hortas().listarPorUsuario(usuario.getId());
        assertEquals(1, hortas.size());
        assertEquals(2, hortas.get(0).getCanteiros().size());
        assertEquals(5, ambiente.servicos.plantios().listarPorHorta(hortas.get(0).getId()).size());
    }

    @Test
    void ehIdempotente() {
        Usuario primeiro = loader.carregar();
        Usuario segundo = loader.carregar();

        assertEquals(primeiro.getId(), segundo.getId());
        assertEquals(1, ambiente.usuarios.listarTodos().size());
        String hortaId = ambiente.servicos.hortas().listarPorUsuario(primeiro.getId()).get(0).getId();
        assertEquals(5, ambiente.servicos.plantios().listarPorHorta(hortaId).size());
    }

    @Test
    void dadosDeDemonstracaoGeramTarefasVariadas() {
        Usuario usuario = loader.carregar();
        String hortaId = ambiente.servicos.hortas().listarPorUsuario(usuario.getId()).get(0).getId();

        List<Tarefa> tarefas = ambiente.servicos.plantios().tarefasDoDia(hortaId, HOJE);

        assertTrue(tarefas.stream().anyMatch(t -> t.tipo() == TipoTarefa.REGA));
        assertTrue(tarefas.stream().anyMatch(t -> t.tipo() == TipoTarefa.CUIDADO));
        assertTrue(tarefas.stream().anyMatch(t -> t.tipo() == TipoTarefa.COLHEITA));
        assertTrue(tarefas.stream().anyMatch(Tarefa::atrasada));
    }
}
