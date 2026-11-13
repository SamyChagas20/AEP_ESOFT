package com.ecohorta.model;

import java.time.LocalDate;

public record JanelaPlantio(int mesInicio, int mesFim, String observacao) {

    private static final String[] MESES =
            {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

    public JanelaPlantio {
        if (mesInicio < 1 || mesInicio > 12 || mesFim < 1 || mesFim > 12) {
            throw new IllegalArgumentException("Os meses devem estar entre 1 e 12.");
        }
        observacao = observacao == null ? "" : observacao.trim();
    }

    public boolean contem(int mes) {
        if (mes < 1 || mes > 12) {
            return false;
        }
        if (mesInicio <= mesFim) {
            return mes >= mesInicio && mes <= mesFim;
        }

        return mes >= mesInicio || mes <= mesFim;
    }

    public boolean contem(LocalDate data) {
        return contem(data.getMonthValue());
    }

    public String descricao() {
        String periodo;
        if (mesInicio == 1 && mesFim == 12) {
            periodo = "Ano todo";
        } else if (mesInicio == mesFim) {
            periodo = nomeDoMes(mesInicio);
        } else {
            periodo = nomeDoMes(mesInicio) + " a " + nomeDoMes(mesFim);
        }
        return observacao.isEmpty() ? periodo : periodo + " (" + observacao + ")";
    }

    public static String nomeDoMes(int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês inválido: " + mes);
        }
        return MESES[mes - 1];
    }
}
