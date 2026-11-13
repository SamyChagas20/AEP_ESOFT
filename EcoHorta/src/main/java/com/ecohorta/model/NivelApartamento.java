package com.ecohorta.model;

/** Quão bem a planta se adapta a apartamentos. */
public enum NivelApartamento {
    IDEAL("Ideal para apartamento"),
    POSSIVEL("Possível com cuidados"),
    NAO_RECOMENDADO("Não recomendado para apartamento");

    private final String rotulo;

    NivelApartamento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
