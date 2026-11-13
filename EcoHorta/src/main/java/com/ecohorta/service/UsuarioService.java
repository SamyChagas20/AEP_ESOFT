package com.ecohorta.service;

import com.ecohorta.model.Usuario;
import com.ecohorta.repository.UsuarioRepository;

import java.util.Optional;

/** Cadastro e consulta de usuários. */
public class UsuarioService {

    private final UsuarioRepository usuarios;

    public UsuarioService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario cadastrar(String nome, String email) {
        Usuario novo = new Usuario(nome, email); // valida os dados
        if (usuarios.buscarPorEmail(novo.getEmail()).isPresent()) {
            throw new RegraDeNegocioException("Já existe um usuário com o e-mail " + novo.getEmail() + ".");
        }
        return usuarios.salvar(novo);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.buscarPorEmail(email);
    }
}
