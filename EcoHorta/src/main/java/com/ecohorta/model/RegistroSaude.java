package com.ecohorta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistroSaude {

    private String id;
    private final String codigo;
    private final TipoRegistroSaude tipo;
    private final String nome;
    private final String descricao;
    private final String orientacao;
    private final String aviso;
    private final List<ItemSaude> evitar = new ArrayList<>();
    private final List<ItemSaude> recomendadas = new ArrayList<>();

    public RegistroSaude(String codigo, TipoRegistroSaude tipo, String nome, String descricao,
                         String orientacao, String aviso) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O código do registro de saúde é obrigatório.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do registro de saúde é obrigatório.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do registro de saúde é obrigatório.");
        }
        this.codigo = codigo.trim().toUpperCase();
        this.tipo = tipo;
        this.nome = nome.trim();
        this.descricao = descricao == null ? "" : descricao.trim();
        this.orientacao = orientacao == null ? "" : orientacao.trim();
        this.aviso = aviso == null ? "" : aviso.trim();
    }

    public RegistroSaude adicionarEvitar(ItemSaude item) {
        if (item.gravidade() == null) {
            throw new IllegalArgumentException("Plantas a evitar precisam de uma gravidade.");
        }
        evitar.add(item);
        return this;
    }

    public RegistroSaude adicionarRecomendada(ItemSaude item) {
        recomendadas.add(item);
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoRegistroSaude getTipo() {
        return tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getOrientacao() {
        return orientacao;
    }

    public String getAviso() {
        return aviso;
    }

    public List<ItemSaude> getEvitar() {
        return Collections.unmodifiableList(evitar);
    }

    public List<ItemSaude> getRecomendadas() {
        return Collections.unmodifiableList(recomendadas);
    }
}
