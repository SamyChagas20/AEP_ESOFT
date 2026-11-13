package com.ecohorta.repository.mongo;

import com.ecohorta.model.Usuario;
import com.ecohorta.repository.UsuarioRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongoUsuarioRepository implements UsuarioRepository {

    public static final String COLECAO = "usuarios";

    private final MongoCollection<Document> colecao;

    public MongoUsuarioRepository(MongoDatabase banco) {
        this.colecao = banco.getCollection(COLECAO);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(new ObjectId().toHexString());
        }
        colecao.replaceOne(Filters.eq("_id", new ObjectId(usuario.getId())),
                UsuarioMapper.paraDocumento(usuario), new ReplaceOptions().upsert(true));
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            return Optional.empty();
        }
        Document doc = colecao.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(doc).map(UsuarioMapper::deDocumento);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        Document doc = colecao.find(Filters.eq("email", Usuario.normalizarEmail(email))).first();
        return Optional.ofNullable(doc).map(UsuarioMapper::deDocumento);
    }

    @Override
    public List<Usuario> listarTodos() {
        return colecao.find().sort(Sorts.ascending("nome"))
                .map(UsuarioMapper::deDocumento)
                .into(new ArrayList<>());
    }
}
