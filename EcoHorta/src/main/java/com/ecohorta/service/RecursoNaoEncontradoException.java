package com.ecohorta.service;

public class RecursoNaoEncontradoException extends RegraDeNegocioException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
