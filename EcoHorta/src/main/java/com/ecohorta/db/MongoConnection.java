package com.ecohorta.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Centraliza a conexão com o MongoDB.
 *
 * Por padrão conecta em um MongoDB local (mongodb://localhost:27017).
 * Se quiser usar o MongoDB Atlas (nuvem), troque a CONNECTION_STRING
 * pela string de conexão fornecida pelo Atlas.
 */
public class MongoConnection {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "PlantaDAO";

    private static MongoClient client;

    private MongoConnection() {
    }

    public static synchronized MongoDatabase getDatabase() {
        if (client == null) {
            client = MongoClients.create(CONNECTION_STRING);
        }
        return client.getDatabase(DATABASE_NAME);
    }

    public static synchronized void fechar() {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
