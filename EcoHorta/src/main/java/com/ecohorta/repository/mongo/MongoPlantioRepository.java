package com.ecohorta.repository.mongo;

import com.ecohorta.model.Plantio;
import com.ecohorta.model.StatusPlantio;
import com.ecohorta.repository.PlantioRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Persistência de plantios (referências + histórico embutido) na coleção "plantios". */
public class MongoPlantioRepository implements PlantioRepository {

    public static final String COLECAO = "plantios";

    private final MongoCollection<Document> colecao;

    public MongoPlantioRepository(MongoDatabase banco) {
        this.colecao = banco.getCollection(COLECAO);
    }

    @Override
    public Plantio salvar(Plantio plantio) {
        if (plantio.getId() == null) {
            plantio.setId(new ObjectId().toHexString());
        }
        colecao.replaceOne(Filters.eq("_id", new ObjectId(plantio.getId())),
                PlantioMapper.paraDocumento(plantio), new ReplaceOptions().upsert(true));
        return plantio;
    }

    @Override
    public Optional<Plantio> buscarPorId(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            return Optional.empty();
        }
        Document doc = colecao.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(doc).map(PlantioMapper::deDocumento);
    }

    @Override
    public List<Plantio> listarPorHorta(String hortaId) {
        if (hortaId == null || !ObjectId.isValid(hortaId)) {
            return List.of();
        }
        return colecao.find(Filters.eq("hortaId", new ObjectId(hortaId)))
                .sort(Sorts.ascending("dataPlantio"))
                .map(PlantioMapper::deDocumento)
                .into(new ArrayList<>());
    }

    /** Usa o pipeline de agregação do MongoDB ($match + $group) para contar por status. */
    @Override
    public Map<StatusPlantio, Long> contarPorStatus(String hortaId) {
        Map<StatusPlantio, Long> resultado = new EnumMap<>(StatusPlantio.class);
        for (StatusPlantio s : StatusPlantio.values()) {
            resultado.put(s, 0L);
        }
        if (hortaId == null || !ObjectId.isValid(hortaId)) {
            return resultado;
        }
        List<Document> grupos = colecao.aggregate(List.of(
                Aggregates.match(Filters.eq("hortaId", new ObjectId(hortaId))),
                Aggregates.group("$status", Accumulators.sum("total", 1))
        )).into(new ArrayList<>());
        for (Document g : grupos) {
            resultado.put(StatusPlantio.valueOf(g.getString("_id")), ((Number) g.get("total")).longValue());
        }
        return resultado;
    }
}
