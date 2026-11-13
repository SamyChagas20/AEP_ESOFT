package com.ecohorta.model;

public enum TipoTarefa {
    REGA("Rega"),
    CUIDADO("Cuidado"),
    COLHEITA("Colheita");

    private final String rotulo;

    TipoTarefa(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
