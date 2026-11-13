package com.ecohorta.model;

/** Tipos de tarefa que o sistema pode sugerir para o dia. */
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
