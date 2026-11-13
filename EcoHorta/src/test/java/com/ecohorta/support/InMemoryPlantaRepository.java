package com.ecohorta.support;

import com.ecohorta.model.Planta;
import com.ecohorta.model.Textos;
import com.ecohorta.repository.FiltroPlantas;
import com.ecohorta.repository.PlantaRepository;
import com.ecohorta.repository.mongo.PlantaMapper;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryPlantaRepository implements PlantaRepository {

    private final Map<String, Document> dados = new LinkedHashMap<>();

    @Override
    public Planta salvar(Planta planta) {
        if (planta.getId() == null) {
            planta.setId(new ObjectId().toHexString());
        }
        dados.put(planta.getId(), PlantaMapper.paraDocumento(planta));
        return planta;
    }

    @Override
    public Optional<Planta> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id)).map(PlantaMapper::deDocumento);
    }

    @Override
    public Optional<Planta> buscarPorNome(String nome) {
        String procurado = Textos.normalizar(nome);
        if (procurado.isEmpty()) {
            return Optional.empty();
        }
        return dados.values().stream()
                .filter(d -> procurado.equals(d.getString("nomeBusca")))
                .findFirst()
                .map(PlantaMapper::deDocumento);
    }

    @Override
    public List<Planta> listarTodas() {
        return filtrar(FiltroPlantas.todas());
    }

    @Override
    public List<Planta> filtrar(FiltroPlantas f) {
        String termo = Textos.normalizar(f.termo());
        return dados.values().stream()
                .map(PlantaMapper::deDocumento)
                .filter(p -> termo.isEmpty() || p.getNomeBusca().contains(termo))
                .filter(p -> f.estacao() == null || p.getEstacoes().contains(f.estacao()))
                .filter(p -> f.nivelLuz() == null || p.getNivelLuz() == f.nivelLuz())
                .filter(p -> f.apartamento() == null || p.getApartamento().nivel() == f.apartamento())
                .filter(p -> f.categoria() == null || p.getCategoria() == f.categoria())
                .filter(p -> f.comestivel() == null || p.isComestivel() == f.comestivel())
                .sorted(Comparator.comparing(Planta::getNomeBusca))
                .toList();
    }

    @Override
    public long contar() {
        return dados.size();
    }
}
