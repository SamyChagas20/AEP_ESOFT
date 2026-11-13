package com.ecohorta.repository.mongo;

import com.ecohorta.model.Plantio;
import com.ecohorta.model.RegistroCuidado;
import com.ecohorta.model.RegistroRega;
import com.ecohorta.model.StatusPlantio;
import com.ecohorta.model.TipoCuidado;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public final class PlantioMapper {

    private PlantioMapper() {
    }

    public static Document paraDocumento(Plantio plantio) {
        Document doc = new Document();
        if (plantio.getId() != null) {
            doc.put("_id", new ObjectId(plantio.getId()));
        }
        doc.append("hortaId", BsonUtil.paraObjectId(plantio.getHortaId()));
        doc.append("plantaId", BsonUtil.paraObjectId(plantio.getPlantaId()));
        doc.append("canteiro", plantio.getCanteiro());
        doc.append("quantidade", plantio.getQuantidade());
        doc.append("dataPlantio", BsonUtil.paraDate(plantio.getDataPlantio()));
        doc.append("status", plantio.getStatus().name());
        doc.append("dataEncerramento", BsonUtil.paraDate(plantio.getDataEncerramento()));

        List<Document> regas = new ArrayList<>();
        for (RegistroRega r : plantio.getRegas()) {
            regas.add(new Document("data", BsonUtil.paraDate(r.data()))
                    .append("quantidadeMl", r.quantidadeMl())
                    .append("observacao", r.observacao()));
        }
        doc.append("regas", regas);

        List<Document> cuidados = new ArrayList<>();
        for (RegistroCuidado c : plantio.getCuidados()) {
            cuidados.add(new Document("data", BsonUtil.paraDate(c.data()))
                    .append("tipo", c.tipo().name())
                    .append("descricao", c.descricao()));
        }
        doc.append("cuidados", cuidados);
        return doc;
    }

    public static Plantio deDocumento(Document doc) {
        List<RegistroRega> regas = new ArrayList<>();
        for (Document r : BsonUtil.lerSubdocumentos(doc, "regas")) {
            regas.add(new RegistroRega(BsonUtil.paraLocalDate(r.getDate("data")),
                    BsonUtil.lerInt(r, "quantidadeMl"), r.getString("observacao")));
        }
        List<RegistroCuidado> cuidados = new ArrayList<>();
        for (Document c : BsonUtil.lerSubdocumentos(doc, "cuidados")) {
            cuidados.add(new RegistroCuidado(BsonUtil.paraLocalDate(c.getDate("data")),
                    TipoCuidado.valueOf(c.getString("tipo")), c.getString("descricao")));
        }
        return Plantio.restaurar(
                BsonUtil.lerId(doc),
                BsonUtil.lerReferencia(doc, "hortaId"),
                BsonUtil.lerReferencia(doc, "plantaId"),
                doc.getString("canteiro"),
                BsonUtil.lerInt(doc, "quantidade"),
                BsonUtil.paraLocalDate(doc.getDate("dataPlantio")),
                StatusPlantio.valueOf(doc.getString("status")),
                BsonUtil.paraLocalDate(doc.getDate("dataEncerramento")),
                regas,
                cuidados);
    }
}
