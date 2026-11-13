package com.ecohorta.model;

/** Mês dentro de uma estação (subdocumento em "tempo"). */
public record MesDaEstacao(int numero, String nome, boolean melhor, String observacao) {

    public MesDaEstacao {
        nome = Meses.nome(numero);
        observacao = observacao == null ? "" : observacao.trim();
    }
}
