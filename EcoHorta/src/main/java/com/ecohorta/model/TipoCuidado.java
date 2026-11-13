package com.ecohorta.model;

public enum TipoCuidado {
    ADUBACAO("Adubação"),
    PODA("Poda"),
    PRAGAS("Controle de pragas"),
    LUMINOSIDADE("Luminosidade"),
    SOLO("Solo"),
    SUSTENTACAO("Sustentação"),
    MANEJO("Manejo e colheita");

    private final String rotulo;

    TipoCuidado(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
