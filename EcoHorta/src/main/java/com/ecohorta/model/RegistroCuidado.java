package com.ecohorta.model;

import java.time.LocalDate;

public record RegistroCuidado(LocalDate data, TipoCuidado tipo, String descricao) {

    public RegistroCuidado {
        if (data == null) {
            throw new IllegalArgumentException("A data do cuidado é obrigatória.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do cuidado é obrigatório.");
        }
        descricao = descricao == null ? "" : descricao.trim();
    }
}
