package com.ecohorta.support;

import com.ecohorta.model.ItemSaude;
import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.TipoRegistroSaude;
import com.ecohorta.repository.SaudeRepository;
import com.ecohorta.repository.mongo.SaudeMapper;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemorySaudeRepository implements SaudeRepository {

    private final Map<String, Document> dados = new LinkedHashMap<>();

    @Override
    public RegistroSaude salvar(RegistroSaude registro) {
        if (registro.getId() == null) {
            registro.setId(new ObjectId().toHexString());
        }
        dados.put(registro.getId(), SaudeMapper.paraDocumento(registro));
        return registro;
    }

    @Override
    public Optional<RegistroSaude> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return dados.values().stream()
                .filter(d -> codigo.trim().toUpperCase().equals(d.getString("codigo")))
                .findFirst()
                .map(SaudeMapper::deDocumento);
    }

    @Override
    public List<RegistroSaude> listar() {
        return dados.values().stream()
                .map(SaudeMapper::deDocumento)
                .sorted(Comparator.comparing(RegistroSaude::getNome))
                .toList();
    }

    @Override
    public List<RegistroSaude> listarPorTipo(TipoRegistroSaude tipo) {
        return listar().stream().filter(r -> r.getTipo() == tipo).toList();
    }

    @Override
    public List<RegistroSaude> listarPorPlanta(String plantaId) {
        return listar().stream()
                .filter(r -> citam(r.getEvitar(), plantaId) || citam(r.getRecomendadas(), plantaId))
                .toList();
    }

    private static boolean citam(List<ItemSaude> itens, String plantaId) {
        return itens.stream().anyMatch(i -> i.plantaId().equals(plantaId));
    }
}
