package com.ecohorta.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MongoConnectionTest {

    @Test
    void usaOPadraoQuandoAVariavelNaoEstaDefinida() {
        assertEquals("mongodb://localhost:27017", MongoConnection.valorOuPadrao(null, MongoConnection.URI_PADRAO));
        assertEquals("ecohorta", MongoConnection.valorOuPadrao("   ", MongoConnection.BANCO_PADRAO));
    }

    @Test
    void usaOValorInformadoSemEspacos() {
        assertEquals("mongodb://servidor:27017", MongoConnection.valorOuPadrao(" mongodb://servidor:27017 ", "x"));
    }
}
