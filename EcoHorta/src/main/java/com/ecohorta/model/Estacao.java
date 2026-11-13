package com.ecohorta.model;

import java.util.List;

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

    public int getOrdem() {
        return ordem;
    }

    public List<Integer> getMeses() {
        return meses;
    }

    public boolean contem(int mes) {
        return meses.contains(mes);
    }

    public static Estacao deMes(int mes) {
        for (Estacao e : values()) {
            if (e.contem(mes)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Mês inválido: " + mes);
    }
}
