package com.ecohorta.service;

/** Violação de uma regra de negócio (ex.: e-mail já cadastrado). */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
