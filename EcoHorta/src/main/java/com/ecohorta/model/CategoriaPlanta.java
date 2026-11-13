package com.ecohorta.model;

public enum CategoriaPlanta {
    FOLHOSA("Folhosa"),
    HORTALICA_FRUTO("Hortaliça de fruto"),
    RAIZ_TUBERCULO("Raiz ou tubérculo"),
    BULBO("Bulbo"),
    ERVA_TEMPERO("Erva ou tempero"),
    LEGUMINOSA("Leguminosa ou grão"),
    FRUTIFERA("Frutífera"),
    MEDICINAL("Medicinal"),
    PANC("PANC (não convencional)"),
    FLOR("Flor comestível ou companheira"),
    ORNAMENTAL("Ornamental (não comestível)"),
    OUTRA("Outra");

    private final String rotulo;

    CategoriaPlanta(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
