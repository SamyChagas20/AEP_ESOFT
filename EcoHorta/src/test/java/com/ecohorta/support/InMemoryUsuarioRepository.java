package com.ecohorta.support;

import com.ecohorta.model.Usuario;
import com.ecohorta.repository.UsuarioRepository;
import com.ecohorta.repository.mongo.UsuarioMapper;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Repositório em memória que guarda documentos BSON (mesmo formato do MongoDB). */
public class InMemoryUsuarioRepository implements UsuarioRepository {

    private final Map<String, Document> dados = new LinkedHashMap<>();

    @Override
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(new ObjectId().toHexString());
        }
        dados.put(usuario.getId(), UsuarioMapper.paraDocumento(usuario));
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id)).map(UsuarioMapper::deDocumento);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String procurado = Usuario.normalizarEmail(email);
        return dados.values().stream()
                .filter(d -> procurado.equals(d.getString("email")))
                .findFirst()
                .map(UsuarioMapper::deDocumento);
    }

    @Override
    public List<Usuario> listarTodos() {
        return dados.values().stream()
                .map(UsuarioMapper::deDocumento)
                .sorted(Comparator.comparing(Usuario::getNome))
                .toList();
    }
}
