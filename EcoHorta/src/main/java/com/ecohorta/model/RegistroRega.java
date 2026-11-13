package com.ecohorta.model;

import java.time.LocalDate;

/** Histórico de uma rega realizada (subdocumento em "plantios"). */
public record RegistroRega(LocalDate data, int quantidadeMl, String observacao) {

    public RegistroRega {
        if (data == null) {
            throw new IllegalArgumentException("A data da rega é obrigatória.");
        }
        if (quantidadeMl < 1) {
            throw new IllegalArgumentException("A quantidade de água deve ser maior que zero.");
        }
        observacao = observacao == null ? "" : observacao.trim();
    }
}
