package com.ecohorta.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioTest {

    @Test
    void normalizaNomeEEmail() {
        Usuario usuario = new Usuario("  Ana Souza ", "  ANA@Exemplo.COM ");
        assertEquals("Ana Souza", usuario.getNome());
        assertEquals("ana@exemplo.com", usuario.getEmail());
    }

    @Test
    void idComecaNuloEPodeSerDefinido() {
        Usuario usuario = new Usuario("Ana", "ana@exemplo.com");
        assertNull(usuario.getId());
        usuario.setId("abc");
        assertEquals("abc", usuario.getId());
    }

    @Test
    void rejeitaNomeEmBranco() {
        assertThrows(IllegalArgumentException.class, () -> new Usuario(" ", "ana@exemplo.com"));
        assertThrows(IllegalArgumentException.class, () -> new Usuario(null, "ana@exemplo.com"));
    }

    @Test
    void rejeitaEmailInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Usuario("Ana", "ana"));
        assertThrows(IllegalArgumentException.class, () -> new Usuario("Ana", "ana@semponto"));
        assertThrows(IllegalArgumentException.class, () -> new Usuario("Ana", null));
    }

    @Test
    void normalizarEmailTrataNulo() {
        assertEquals("", Usuario.normalizarEmail(null));
        assertEquals("a@b.com", Usuario.normalizarEmail(" A@B.com "));
    }
}
