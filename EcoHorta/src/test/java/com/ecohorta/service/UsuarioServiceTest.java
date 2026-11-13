package com.ecohorta.service;

import com.ecohorta.model.Usuario;
import com.ecohorta.support.Ambiente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioServiceTest {

    private final Ambiente ambiente = new Ambiente();
    private final UsuarioService service = ambiente.servicos.usuarios();

    @Test
    void cadastraUsuarioGerandoId() {
        Usuario usuario = service.cadastrar("Ana", "ANA@exemplo.com");
        assertNotNull(usuario.getId());
        assertEquals("ana@exemplo.com", usuario.getEmail());
    }

    @Test
    void buscaPorEmailIgnorandoMaiusculas() {
        service.cadastrar("Ana", "ana@exemplo.com");
        assertTrue(service.buscarPorEmail("  Ana@Exemplo.com ").isPresent());
        assertTrue(service.buscarPorEmail("outra@exemplo.com").isEmpty());
    }

    @Test
    void naoPermiteEmailDuplicado() {
        service.cadastrar("Ana", "ana@exemplo.com");
        assertThrows(RegraDeNegocioException.class, () -> service.cadastrar("Outra Ana", "ANA@exemplo.com"));
    }

    @Test
    void propagaValidacaoDoModelo() {
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar("Ana", "invalido"));
    }

    @Test
    void repositorioListaUsuariosOrdenadosPorNome() {
        service.cadastrar("Bia", "bia@exemplo.com");
        service.cadastrar("Ana", "ana@exemplo.com");
        assertEquals("Ana", ambiente.usuarios.listarTodos().get(0).getNome());
    }
}
