package com.ecohorta.repository.mongo;

import com.ecohorta.model.Canteiro;
import com.ecohorta.model.Horta;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public final class HortaMapper {

    private HortaMapper() {
    }

    public static Document paraDocumento(Horta horta) {
        Document doc = new Document();
        if (horta.getId() != null) {
            doc.put("_id", new ObjectId(horta.getId()));
        }
        doc.append("usuarioId", BsonUtil.paraObjectId(horta.getUsuarioId()));
        doc.append("nome", horta.getNome());
        doc.append("localizacao", horta.getLocalizacao());

        List<Document> canteiros = new ArrayList<>();
        for (Canteiro c : horta.getCanteiros()) {
            canteiros.add(new Document("nome", c.nome())
                    .append("areaM2", c.areaM2())
                    .append("exposicaoSol", c.exposicaoSol()));
        }
        doc.append("canteiros", canteiros);
        return doc;
    }

    public static Horta deDocumento(Document doc) {
        Horta horta = new Horta(
                BsonUtil.lerReferencia(doc, "usuarioId"),
                doc.getString("nome"),
                doc.getString("localizacao"));
        horta.setId(BsonUtil.lerId(doc));
        for (Document c : BsonUtil.lerSubdocumentos(doc, "canteiros")) {
            horta.adicionarCanteiro(new Canteiro(
                    c.getString("nome"),
                    BsonUtil.lerDouble(c, "areaM2"),
                    c.getString("exposicaoSol")));
        }
        return horta;
    }
}
