package com.ecohorta.service;

import com.ecohorta.model.Horta;
import com.ecohorta.model.Usuario;
import com.ecohorta.support.Ambiente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HortaServiceTest {

    private final Ambiente ambiente = new Ambiente();
    private final HortaService service = ambiente.servicos.hortas();
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        usuario = ambiente.servicos.usuarios().cadastrar("Ana", "ana@exemplo.com");
    }

    @Test
    void criaHortaLigadaAoUsuario() {
        Horta horta = service.criarHorta(usuario.getId(), "Quintal", "Maringá");
        assertEquals(usuario.getId(), horta.getUsuarioId());
        assertEquals(1, service.listarPorUsuario(usuario.getId()).size());
    }

    @Test
    void naoCriaHortaParaUsuarioInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.criarHorta("000000000000000000000000", "Quintal", ""));
    }

    @Test
    void adicionaCanteirosEPersiste() {
        Horta horta = service.criarHorta(usuario.getId(), "Quintal", "");
        service.adicionarCanteiro(horta.getId(), "Sol", 4.0, "Sol pleno");
        service.adicionarCanteiro(horta.getId(), "Sombra", 2.0, "Meia sombra");

        Horta recarregada = service.buscarPorId(horta.getId());
        assertEquals(2, recarregada.getCanteiros().size());
        assertTrue(recarregada.buscarCanteiro("Sombra").isPresent());
    }

    @Test
    void rejeitaCanteiroRepetidoEHortaInexistente() {
        Horta horta = service.criarHorta(usuario.getId(), "Quintal", "");
        service.adicionarCanteiro(horta.getId(), "Sol", 4.0, "");
        assertThrows(IllegalArgumentException.class,
                () -> service.adicionarCanteiro(horta.getId(), "sol", 1.0, ""));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.adicionarCanteiro("000000000000000000000000", "X", 1.0, ""));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId("nao-existe"));
    }

    @Test
    void listaApenasAsHortasDoUsuario() {
        Usuario outro = ambiente.servicos.usuarios().cadastrar("Bia", "bia@exemplo.com");
        service.criarHorta(usuario.getId(), "Da Ana", "");
        service.criarHorta(outro.getId(), "Da Bia", "");
        assertEquals(1, service.listarPorUsuario(usuario.getId()).size());
        assertEquals("Da Bia", service.listarPorUsuario(outro.getId()).get(0).getNome());
    }
}
