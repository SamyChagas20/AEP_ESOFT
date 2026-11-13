package com.ecohorta.repository;

import com.ecohorta.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(String id);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> listarTodos();
}
