package com.ecohorta.repository;

import com.ecohorta.model.Usuario;

import java.util.List;
import java.util.Optional;

/** Contrato de persistência de usuários (coleção "usuarios"). */
public interface UsuarioRepository {

    /** Insere ou atualiza o usuário; gera o id se ainda não existir. */
    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(String id);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> listarTodos();
}
