package com.ecohorta.repository.mongo;

import com.ecohorta.model.Estacao;
import com.ecohorta.model.Tempo;
import com.ecohorta.repository.TempoRepository;
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

/** Persistência das estações do ano na coleção "tempo". */
public class MongoTempoRepository implements TempoRepository {

    public static final String COLECAO = "tempo";

    private final MongoCollection<Document> colecao;

    public MongoTempoRepository(MongoDatabase banco) {
        this.colecao = banco.getCollection(COLECAO);
    }

    @Override
    public Tempo salvar(Tempo tempo) {
        if (tempo.getId() == null) {
            tempo.setId(new ObjectId().toHexString());
        }
        colecao.replaceOne(Filters.eq("_id", new ObjectId(tempo.getId())),
                TempoMapper.paraDocumento(tempo), new ReplaceOptions().upsert(true));
        return tempo;
    }

    @Override
    public Optional<Tempo> buscarPorEstacao(Estacao estacao) {
        Document doc = colecao.find(Filters.eq("codigo", estacao.name())).first();
        return Optional.ofNullable(doc).map(TempoMapper::deDocumento);
    }

    @Override
    public List<Tempo> listar() {
        return colecao.find().sort(Sorts.ascending("ordem"))
                .map(TempoMapper::deDocumento)
                .into(new ArrayList<>());
    }
}
