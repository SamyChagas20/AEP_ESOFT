package com.ecohorta.model;

public enum TipoRegistroSaude {
    ANIMAL("Animais de estimação"),
    PESSOA("Pessoas"),
    COMORBIDADE("Condição de saúde");

    private final String rotulo;

    TipoRegistroSaude(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
