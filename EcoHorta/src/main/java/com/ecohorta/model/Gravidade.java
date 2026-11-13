package com.ecohorta.model;

/**
 * Intensidade do alerta de saúde.
 * Para toxicidade: LEVE = incômodo digestivo/irritação; MODERADA = intoxicação provável; GRAVE = risco de vida.
 * Para comorbidades: LEVE = moderar a porção; MODERADA = limitar bastante; GRAVE = evitar.
 */
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
