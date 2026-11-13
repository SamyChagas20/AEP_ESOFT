package com.ecohorta.model;

public record ItemSaude(String plantaId, String plantaNome, String motivo, Gravidade gravidade) {

    public ItemSaude {
        if (plantaId == null || plantaId.isBlank()) {
            throw new IllegalArgumentException("A planta do item de saúde é obrigatória.");
        }
        if (plantaNome == null || plantaNome.isBlank()) {
            throw new IllegalArgumentException("O nome da planta é obrigatório.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("O motivo é obrigatório.");
        }
        motivo = motivo.trim();
    }
}
