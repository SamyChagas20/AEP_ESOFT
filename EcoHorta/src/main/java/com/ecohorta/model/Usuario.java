package com.ecohorta.model;

import java.util.Locale;
import java.util.regex.Pattern;

public class Usuario {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private String id;
    private final String nome;
    private final String email;

    public Usuario(String nome, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        this.email = normalizarEmail(email);
        if (!EMAIL.matcher(this.email).matches()) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        this.nome = nome.trim();
    }

    public static String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
