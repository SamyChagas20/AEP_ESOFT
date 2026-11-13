package com.ecohorta.model;

public enum Gravidade {
    LEVE("Leve"),
    MODERADA("Moderada"),
    GRAVE("Grave");

    private final String rotulo;

    Gravidade(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
