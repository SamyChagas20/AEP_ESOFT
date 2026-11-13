package com.ecohorta.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

public class MongoConnection implements AutoCloseable {

    public static final String URI_PADRAO = "mongodb://localhost:27017";
    public static final String BANCO_PADRAO = "ecohorta";
    private static final int TEMPO_LIMITE_SEGUNDOS = 3;

    private final MongoClient cliente;
    private final MongoDatabase banco;

    public MongoConnection(String uri, String nomeBanco) {
        MongoClientSettings configuracao = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .applyToClusterSettings(c -> c.serverSelectionTimeout(TEMPO_LIMITE_SEGUNDOS, TimeUnit.SECONDS))
                .build();
        this.cliente = MongoClients.create(configuracao);
        this.banco = cliente.getDatabase(nomeBanco);
    }

    public static MongoConnection deAmbiente() {
        return new MongoConnection(
                valorOuPadrao(System.getenv("MONGODB_URI"), URI_PADRAO),
                valorOuPadrao(System.getenv("MONGODB_DB"), BANCO_PADRAO));
    }

    static String valorOuPadrao(String valor, String padrao) {
        return (valor == null || valor.isBlank()) ? padrao : valor.trim();
    }

    public MongoDatabase getBanco() {
        return banco;
    }

    public void verificarConexao() {
        banco.runCommand(new Document("ping", 1));
    }

    @Override
    public void close() {
        cliente.close();
    }
}
