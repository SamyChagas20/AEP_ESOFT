package com.ecohorta.model;

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
