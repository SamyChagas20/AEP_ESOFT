package com.ecohorta.dao;

import com.ecohorta.db.MongoConnection;
import com.ecohorta.model.Planta;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por toda a comunicação com a coleção "plantas" no MongoDB.
 * Converte entre Document (formato do Mongo) e a classe Planta (modelo Java).
 */
public class PlantaDAO {

    private static final String COLLECTION = "plantas";

    private MongoCollection<Document> getCollection() {
        MongoDatabase db = MongoConnection.getDatabase();
        return db.getCollection(COLLECTION);
    }

    /** Converte um Document do Mongo em um objeto Planta. */
    private Planta documentoParaPlanta(Document doc) {
        Planta p = new Planta();
        p.setNome(doc.getString("nome"));
        p.setEpocaPlantio(doc.getString("epocaPlantio"));
        p.setFrequenciaRegaDias(doc.getInteger("frequenciaRegaDias", 1));
        p.setDiasParaColheita(doc.getInteger("diasParaColheita", 0));
        p.setCuidados(doc.getString("cuidados"));

        String ultimaRegaStr = doc.getString("ultimaRega");
        if (ultimaRegaStr != null && !ultimaRegaStr.isBlank()) {
            p.setUltimaRega(LocalDate.parse(ultimaRegaStr));
        }
        return p;
    }

    /** Converte um objeto Planta em um Document para salvar no Mongo. */
    private Document plantaParaDocumento(Planta p) {
        Document doc = new Document();
        doc.append("nome", p.getNome());
        doc.append("epocaPlantio", p.getEpocaPlantio());
        doc.append("frequenciaRegaDias", p.getFrequenciaRegaDias());
        doc.append("diasParaColheita", p.getDiasParaColheita());
        doc.append("cuidados", p.getCuidados());
        doc.append("ultimaRega", p.getUltimaRega() == null ? null : p.getUltimaRega().toString());
        return doc;
    }

    /** Retorna true se a coleção de plantas estiver vazia. */
    public boolean colecaoVazia() {
        return getCollection().countDocuments() == 0;
    }

    /** Insere uma nova planta (usado tanto no seed inicial quanto para cadastro manual). */
    public void inserir(Planta planta) {
        getCollection().insertOne(plantaParaDocumento(planta));
    }

    /** Lista todas as plantas cadastradas, ordenadas por nome. */
    public List<Planta> listarTodas() {
        List<Planta> plantas = new ArrayList<>();
        for (Document doc : getCollection().find().sort(new Document("nome", 1))) {
            plantas.add(documentoParaPlanta(doc));
        }
        return plantas;
    }

    /** Busca uma planta pelo nome (case-insensitive). */
    public Planta buscarPorNome(String nome) {
        Document doc = getCollection()
                .find(Filters.regex("nome", "^" + nome + "$", "i"))
                .first();
        return doc == null ? null : documentoParaPlanta(doc);
    }

    /** Atualiza a data da última rega de uma planta para hoje. */
    public boolean registrarRega(String nome, LocalDate data) {
        var resultado = getCollection().updateOne(
                Filters.regex("nome", "^" + nome + "$", "i"),
                Updates.set("ultimaRega", data.toString())
        );
        return resultado.getModifiedCount() > 0;
    }

    /** Remove todas as plantas (útil para resetar o banco durante testes). */
    public void limparTudo() {
        getCollection().deleteMany(new Document());
    }
}
