package com.ecohorta.model;

import java.text.Normalizer;
import java.util.Locale;

public final class Textos {

    private Textos() {
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return semAcento.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }
}
