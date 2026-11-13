package com.ecohorta.support;

import com.ecohorta.model.Horta;
import com.ecohorta.repository.HortaRepository;
import com.ecohorta.repository.mongo.HortaMapper;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryHortaRepository implements HortaRepository {

    private final Map<String, Document> dados = new LinkedHashMap<>();

    @Override
    public Horta salvar(Horta horta) {
        if (horta.getId() == null) {
            horta.setId(new ObjectId().toHexString());
        }
        dados.put(horta.getId(), HortaMapper.paraDocumento(horta));
        return horta;
    }

    @Override
    public Optional<Horta> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id)).map(HortaMapper::deDocumento);
    }

    @Override
    public List<Horta> listarPorUsuario(String usuarioId) {
        return dados.values().stream()
                .filter(d -> d.getObjectId("usuarioId").toHexString().equals(usuarioId))
                .map(HortaMapper::deDocumento)
                .sorted(Comparator.comparing(Horta::getNome))
                .toList();
    }
}
