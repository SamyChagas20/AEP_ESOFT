package com.ecohorta.model;

/**
 * Divisão física de uma horta. É um item da lista de subdocumentos
 * "canteiros" dentro do documento da horta.
 */
public record Canteiro(String nome, double areaM2, String exposicaoSol) {

    public Canteiro {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do canteiro é obrigatório.");
        }
        if (areaM2 <= 0) {
            throw new IllegalArgumentException("A área do canteiro deve ser maior que zero.");
        }
        nome = nome.trim();
        exposicaoSol = (exposicaoSol == null || exposicaoSol.isBlank()) ? "Não informada" : exposicaoSol.trim();
    }
}
