package com.ecohorta.model;

import java.util.List;

/**
 * Estações do ano (hemisfério sul), por meses inteiros:
 * primavera set-nov, verão dez-fev, outono mar-mai, inverno jun-ago.
 */
public enum Estacao {
    PRIMAVERA("Primavera", 1, 9, 10, 11),
    VERAO("Verão", 2, 12, 1, 2),
    OUTONO("Outono", 3, 3, 4, 5),
    INVERNO("Inverno", 4, 6, 7, 8);

    private final String nome;
    private final int ordem;
    private final List<Integer> meses;

    Estacao(String nome, int ordem, Integer... meses) {
        this.nome = nome;
        this.ordem = ordem;
        this.meses = List.of(meses);
    }

    public String getNome() {
        return nome;
    }

    /** Posição para ordenação (1 = primavera). */
    public int getOrdem() {
        return ordem;
    }

    /** Meses (1-12) da estação, na ordem cronológica. */
    public List<Integer> getMeses() {
        return meses;
    }

    public boolean contem(int mes) {
        return meses.contains(mes);
    }

    /** Estação a que o mês (1-12) pertence. */
    public static Estacao deMes(int mes) {
        for (Estacao e : values()) {
            if (e.contem(mes)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Mês inválido: " + mes);
    }
}
