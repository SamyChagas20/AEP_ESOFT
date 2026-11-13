package com.ecohorta.support;

import com.ecohorta.model.Estacao;
import com.ecohorta.model.Tempo;
import com.ecohorta.repository.TempoRepository;
import com.ecohorta.repository.mongo.TempoMapper;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryTempoRepository implements TempoRepository {

    private final Map<String, Document> dados = new LinkedHashMap<>();

    @Override
    public Tempo salvar(Tempo tempo) {
        if (tempo.getId() == null) {
            tempo.setId(new ObjectId().toHexString());
        }
        dados.put(tempo.getId(), TempoMapper.paraDocumento(tempo));
        return tempo;
    }

    @Override
    public Optional<Tempo> buscarPorEstacao(Estacao estacao) {
        return dados.values().stream()
                .filter(d -> estacao.name().equals(d.getString("codigo")))
                .findFirst()
                .map(TempoMapper::deDocumento);
    }

    @Override
    public List<Tempo> listar() {
        return dados.values().stream()
                .map(TempoMapper::deDocumento)
                .sorted(Comparator.comparingInt(t -> t.getEstacao().getOrdem()))
                .toList();
    }
}
