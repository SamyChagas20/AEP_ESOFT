package com.ecohorta.repository.mongo;

import com.ecohorta.model.Horta;
import com.ecohorta.repository.HortaRepository;
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

/** Persistência de hortas (com canteiros embutidos) na coleção "hortas". */
public class MongoHortaRepository implements HortaRepository {

    public static final String COLECAO = "hortas";

    private final MongoCollection<Document> colecao;

    public MongoHortaRepository(MongoDatabase banco) {
        this.colecao = banco.getCollection(COLECAO);
    }

    @Override
    public Horta salvar(Horta horta) {
        if (horta.getId() == null) {
            horta.setId(new ObjectId().toHexString());
        }
        colecao.replaceOne(Filters.eq("_id", new ObjectId(horta.getId())),
                HortaMapper.paraDocumento(horta), new ReplaceOptions().upsert(true));
        return horta;
    }

    @Override
    public Optional<Horta> buscarPorId(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            return Optional.empty();
        }
        Document doc = colecao.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(doc).map(HortaMapper::deDocumento);
    }

    @Override
    public List<Horta> listarPorUsuario(String usuarioId) {
        if (usuarioId == null || !ObjectId.isValid(usuarioId)) {
            return List.of();
        }
        return colecao.find(Filters.eq("usuarioId", new ObjectId(usuarioId)))
                .sort(Sorts.ascending("nome"))
                .map(HortaMapper::deDocumento)
                .into(new ArrayList<>());
    }
}
