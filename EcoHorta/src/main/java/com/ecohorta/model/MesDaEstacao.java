package com.ecohorta.model;

public record MesDaEstacao(int numero, String nome, boolean melhor, String observacao) {

    public MesDaEstacao {
        nome = Meses.nome(numero);
        observacao = observacao == null ? "" : observacao.trim();
    }
}
