package com.ecohorta.service;

/** Registro referenciado não existe no banco (usuário, horta, planta, plantio...). */
public class RecursoNaoEncontradoException extends RegraDeNegocioException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
