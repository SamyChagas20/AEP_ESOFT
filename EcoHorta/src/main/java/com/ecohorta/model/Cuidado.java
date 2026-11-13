package com.ecohorta.model;

public record Cuidado(TipoCuidado tipo, String descricao, int frequenciaDias) {

    public Cuidado {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do cuidado é obrigatório.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição do cuidado é obrigatória.");
        }
        if (frequenciaDias < 0) {
            throw new IllegalArgumentException("A frequência do cuidado não pode ser negativa.");
        }
        descricao = descricao.trim();
    }

    public boolean periodico() {
        return frequenciaDias > 0;
    }
}
