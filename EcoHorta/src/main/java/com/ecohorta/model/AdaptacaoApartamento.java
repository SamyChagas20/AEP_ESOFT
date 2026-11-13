package com.ecohorta.model;

/** Adaptação a apartamento (documento aninhado em "plantas"). */
public record AdaptacaoApartamento(NivelApartamento nivel, String observacao) {

    public AdaptacaoApartamento {
        if (nivel == null) {
            throw new IllegalArgumentException("O nível de adaptação ao apartamento é obrigatório.");
        }
        observacao = observacao == null ? "" : observacao.trim();
    }
}
