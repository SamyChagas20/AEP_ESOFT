package com.ecohorta.model;

/**
 * Recomendação de rega de uma planta.
 * É um subdocumento aninhado (documento dentro de documento) em "plantas".
 */
public record RegaRecomendada(int frequenciaDias, int quantidadeMl, String observacao) {

    public RegaRecomendada {
        if (frequenciaDias < 1) {
            throw new IllegalArgumentException("A frequência de rega deve ser de pelo menos 1 dia.");
        }
        if (quantidadeMl < 1) {
            throw new IllegalArgumentException("A quantidade de água deve ser maior que zero.");
        }
        observacao = observacao == null ? "" : observacao.trim();
    }
}
