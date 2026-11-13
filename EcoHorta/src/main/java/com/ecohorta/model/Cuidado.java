package com.ecohorta.model;

/**
 * Cuidado recomendado para uma planta (adubação, poda, pragas...).
 * Se frequenciaDias for 0, é apenas uma orientação (não gera tarefas periódicas).
 * É um item da lista de subdocumentos "cuidados" em "plantas".
 */
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

    /** Verdadeiro se o cuidado se repete periodicamente. */
    public boolean periodico() {
        return frequenciaDias > 0;
    }
}
