package com.ecohorta.repository.mongo;

import com.ecohorta.model.Planta;
import com.ecohorta.model.Textos;
import com.ecohorta.repository.FiltroPlantas;
import com.ecohorta.repository.PlantaRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class MongoPlantaRepository implements PlantaRepository {

    public static final String COLECAO = "plantas";

    private final MongoCollection<Document> colecao;

    public MongoPlantaRepository(MongoDatabase banco) {
        this.colecao = banco.getCollection(COLECAO);
    }

    @Override
    public Planta salvar(Planta planta) {
        if (planta.getId() == null) {
            planta.setId(new ObjectId().toHexString());
        }
        colecao.replaceOne(Filters.eq("_id", new ObjectId(planta.getId())),
                PlantaMapper.paraDocumento(planta), new ReplaceOptions().upsert(true));
        return planta;
    }

    @Override
    public Optional<Planta> buscarPorId(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            return Optional.empty();
        }
        Document doc = colecao.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(doc).map(PlantaMapper::deDocumento);
    }

    @Override
    public Optional<Planta> buscarPorNome(String nome) {
        String procurado = Textos.normalizar(nome);
        if (procurado.isEmpty()) {
            return Optional.empty();
        }
        Document doc = colecao.find(Filters.eq("nomeBusca", procurado)).first();
        return Optional.ofNullable(doc).map(PlantaMapper::deDocumento);
    }

    @Override
    public List<Planta> listarTodas() {
        return filtrar(FiltroPlantas.todas());
    }

    @Override
    public List<Planta> filtrar(FiltroPlantas filtro) {
        List<Bson> condicoes = new ArrayList<>();
        String termo = Textos.normalizar(filtro.termo());
        if (!termo.isEmpty()) {
            condicoes.add(Filters.regex("nomeBusca", Pattern.compile(Pattern.quote(termo))));
        }
        if (filtro.estacao() != null) {
            condicoes.add(Filters.eq("estacoes", filtro.estacao().name()));
        }
        if (filtro.nivelLuz() != null) {
            condicoes.add(Filters.eq("nivelLuz", filtro.nivelLuz().name()));
        }
        if (filtro.apartamento() != null) {
            condicoes.add(Filters.eq("apartamento.nivel", filtro.apartamento().name()));
        }
        if (filtro.categoria() != null) {
            condicoes.add(Filters.eq("categoria", filtro.categoria().name()));
        }
        if (filtro.comestivel() != null) {
            condicoes.add(Filters.eq("comestivel", filtro.comestivel()));
        }
        Bson consulta = condicoes.isEmpty() ? new Document() : Filters.and(condicoes);
        return colecao.find(consulta).sort(Sorts.ascending("nomeBusca"))
                .map(PlantaMapper::deDocumento)
                .into(new ArrayList<>());
    }

    @Override
    public long contar() {
        return colecao.countDocuments();
    }
}
