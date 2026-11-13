package com.ecohorta.model;

/** Categorias de cuidado que uma planta pode exigir. */
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
