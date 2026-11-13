package com.ecohorta.cli;

/** Sinaliza que a entrada de dados acabou (ex.: Ctrl+D ou fim de um script de testes). */
class EntradaEncerradaException extends RuntimeException {

    EntradaEncerradaException() {
        super("Entrada encerrada");
    }
}
