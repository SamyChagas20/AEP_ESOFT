package com.ecohorta.model;

import java.text.Normalizer;
import java.util.Locale;

/** Utilitário de texto para buscas sem diferença de acentos e maiúsculas. */
public final class Textos {

    private Textos() {
    }

    /** "  Açafrão  " -> "acafrao". Nulo vira texto vazio. */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return semAcento.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }
}
