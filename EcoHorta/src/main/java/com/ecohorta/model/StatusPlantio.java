package com.ecohorta.model;

/** Situação de um plantio ao longo do seu ciclo de vida. */
public enum StatusPlantio {
    ATIVO("Ativo"),
    COLHIDO("Colhido"),
    PERDIDO("Perdido");

    private final String rotulo;

    StatusPlantio(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
