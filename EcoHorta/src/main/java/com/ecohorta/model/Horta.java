package com.ecohorta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Horta {

    private String id;
    private final String usuarioId;
    private final String nome;
    private final String localizacao;
    private final List<Canteiro> canteiros = new ArrayList<>();

    public Horta(String usuarioId, String nome, String localizacao) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException("A horta precisa pertencer a um usuário.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da horta é obrigatório.");
        }
        this.usuarioId = usuarioId;
        this.nome = nome.trim();
        this.localizacao = (localizacao == null || localizacao.isBlank()) ? "Não informada" : localizacao.trim();
    }

    public Horta adicionarCanteiro(Canteiro canteiro) {
        if (buscarCanteiro(canteiro.nome()).isPresent()) {
            throw new IllegalArgumentException("Já existe um canteiro chamado \"" + canteiro.nome() + "\" nesta horta.");
        }
        canteiros.add(canteiro);
        return this;
    }

    public Optional<Canteiro> buscarCanteiro(String nomeCanteiro) {
        if (nomeCanteiro == null) {
            return Optional.empty();
        }
        return canteiros.stream()
                .filter(c -> c.nome().equalsIgnoreCase(nomeCanteiro.trim()))
                .findFirst();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public List<Canteiro> getCanteiros() {
        return Collections.unmodifiableList(canteiros);
    }
}
