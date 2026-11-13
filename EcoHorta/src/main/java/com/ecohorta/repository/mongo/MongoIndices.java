package com.ecohorta.repository.mongo;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public final class MongoIndices {

    private MongoIndices() {
    }

    public static void criar(MongoDatabase banco) {
        banco.getCollection(MongoUsuarioRepository.COLECAO)
                .createIndex(Indexes.ascending("email"), new IndexOptions().unique(true));

        var plantas = banco.getCollection(MongoPlantaRepository.COLECAO);
        plantas.createIndex(Indexes.ascending("nomeBusca"), new IndexOptions().unique(true));
        plantas.createIndex(Indexes.ascending("estacoes"));
        plantas.createIndex(Indexes.ascending("categoria"));
        plantas.createIndex(Indexes.ascending("nivelLuz"));
        plantas.createIndex(Indexes.ascending("apartamento.nivel"));

        banco.getCollection(MongoTempoRepository.COLECAO)
                .createIndex(Indexes.ascending("codigo"), new IndexOptions().unique(true));

        var saude = banco.getCollection(MongoSaudeRepository.COLECAO);
        saude.createIndex(Indexes.ascending("codigo"), new IndexOptions().unique(true));
        saude.createIndex(Indexes.ascending("evitar.plantaId"));
        saude.createIndex(Indexes.ascending("recomendadas.plantaId"));

        banco.getCollection(MongoHortaRepository.COLECAO)
                .createIndex(Indexes.ascending("usuarioId"));

        var plantios = banco.getCollection(MongoPlantioRepository.COLECAO);
        plantios.createIndex(Indexes.ascending("hortaId"));
        plantios.createIndex(Indexes.ascending("plantaId"));
    }
}
