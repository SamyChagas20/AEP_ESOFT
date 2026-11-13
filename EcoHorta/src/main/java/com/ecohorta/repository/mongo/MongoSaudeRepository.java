package com.ecohorta.repository.mongo;

import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.TipoRegistroSaude;
import com.ecohorta.repository.SaudeRepository;
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

/** Persistência de saúde e segurança na coleção "saude". */
public class MongoSaudeRepository implements SaudeRepository {

    public static final String COLECAO = "saude";

    private final MongoCollection<Document> colecao;

    public MongoSaudeRepository(MongoDatabase banco) {
        this.colecao = banco.getCollection(COLECAO);
    }

    @Override
    public RegistroSaude salvar(RegistroSaude registro) {
        if (registro.getId() == null) {
            registro.setId(new ObjectId().toHexString());
        }
        colecao.replaceOne(Filters.eq("_id", new ObjectId(registro.getId())),
                SaudeMapper.paraDocumento(registro), new ReplaceOptions().upsert(true));
        return registro;
    }

    @Override
    public Optional<RegistroSaude> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        Document doc = colecao.find(Filters.eq("codigo", codigo.trim().toUpperCase())).first();
        return Optional.ofNullable(doc).map(SaudeMapper::deDocumento);
    }

    @Override
    public List<RegistroSaude> listar() {
        return colecao.find().sort(Sorts.ascending("nome"))
                .map(SaudeMapper::deDocumento)
                .into(new ArrayList<>());
    }

    @Override
    public List<RegistroSaude> listarPorTipo(TipoRegistroSaude tipo) {
        return colecao.find(Filters.eq("tipo", tipo.name())).sort(Sorts.ascending("nome"))
                .map(SaudeMapper::deDocumento)
                .into(new ArrayList<>());
    }

    /** Consulta nas listas embutidas: procura o id da planta em "evitar" ou "recomendadas". */
    @Override
    public List<RegistroSaude> listarPorPlanta(String plantaId) {
        if (plantaId == null || !ObjectId.isValid(plantaId)) {
            return List.of();
        }
        ObjectId id = new ObjectId(plantaId);
        return colecao.find(Filters.or(Filters.eq("evitar.plantaId", id),
                        Filters.eq("recomendadas.plantaId", id)))
                .sort(Sorts.ascending("nome"))
                .map(SaudeMapper::deDocumento)
                .into(new ArrayList<>());
    }
}
