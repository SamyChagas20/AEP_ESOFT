package com.ecohorta.support;

import com.ecohorta.model.Plantio;
import com.ecohorta.model.StatusPlantio;
import com.ecohorta.repository.PlantioRepository;
import com.ecohorta.repository.mongo.PlantioMapper;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryPlantioRepository implements PlantioRepository {

    private final Map<String, Document> dados = new LinkedHashMap<>();

    @Override
    public Plantio salvar(Plantio plantio) {
        if (plantio.getId() == null) {
            plantio.setId(new ObjectId().toHexString());
        }
        dados.put(plantio.getId(), PlantioMapper.paraDocumento(plantio));
        return plantio;
    }

    @Override
    public Optional<Plantio> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id)).map(PlantioMapper::deDocumento);
    }

    @Override
    public List<Plantio> listarPorHorta(String hortaId) {
        return dados.values().stream()
                .filter(d -> d.getObjectId("hortaId").toHexString().equals(hortaId))
                .map(PlantioMapper::deDocumento)
                .sorted(Comparator.comparing(Plantio::getDataPlantio))
                .toList();
    }

    @Override
    public Map<StatusPlantio, Long> contarPorStatus(String hortaId) {
        Map<StatusPlantio, Long> resultado = new EnumMap<>(StatusPlantio.class);
        for (StatusPlantio s : StatusPlantio.values()) {
            resultado.put(s, 0L);
        }
        for (Plantio p : listarPorHorta(hortaId)) {
            resultado.merge(p.getStatus(), 1L, Long::sum);
        }
        return resultado;
    }
}
