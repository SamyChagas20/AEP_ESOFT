package com.ecohorta.model;

/** Nomes dos meses em português. */
public final class Meses {

    private static final String[] NOMES = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

    private Meses() {
    }

    public static String nome(int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês inválido: " + mes);
        }
        return NOMES[mes - 1];
    }
}
