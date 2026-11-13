package com.ecohorta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tempo {

    private String id;
    private final Estacao estacao;
    private final String descricao;
    private final int melhorMes;
    private final String justificativaMelhorMes;
    private final List<MesDaEstacao> meses = new ArrayList<>();

    public Tempo(Estacao estacao, String descricao, int melhorMes, String justificativaMelhorMes) {
        if (estacao == null) {
            throw new IllegalArgumentException("A estação é obrigatória.");
        }
        if (!estacao.contem(melhorMes)) {
            throw new IllegalArgumentException("O melhor mês deve pertencer à estação " + estacao.getNome() + ".");
        }
        this.estacao = estacao;
        this.descricao = descricao == null ? "" : descricao.trim();
        this.melhorMes = melhorMes;
        this.justificativaMelhorMes = justificativaMelhorMes == null ? "" : justificativaMelhorMes.trim();
    }

    public Tempo adicionarMes(int mes, String observacao) {
        if (!estacao.contem(mes)) {
            throw new IllegalArgumentException("O mês " + mes + " não pertence à estação " + estacao.getNome() + ".");
        }
        meses.add(new MesDaEstacao(mes, Meses.nome(mes), mes == melhorMes, observacao));
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getMelhorMes() {
        return melhorMes;
    }

    public String getJustificativaMelhorMes() {
        return justificativaMelhorMes;
    }

    public List<MesDaEstacao> getMeses() {
        return Collections.unmodifiableList(meses);
    }
}
