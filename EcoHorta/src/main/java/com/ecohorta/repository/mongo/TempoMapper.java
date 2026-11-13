package com.ecohorta.repository.mongo;

import com.ecohorta.model.Estacao;
import com.ecohorta.model.MesDaEstacao;
import com.ecohorta.model.Tempo;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public final class TempoMapper {

    private TempoMapper() {
    }

    public static Document paraDocumento(Tempo tempo) {
        Document doc = new Document();
        if (tempo.getId() != null) {
            doc.put("_id", new ObjectId(tempo.getId()));
        }
        doc.append("codigo", tempo.getEstacao().name());
        doc.append("nome", tempo.getEstacao().getNome());
        doc.append("ordem", tempo.getEstacao().getOrdem());
        doc.append("descricao", tempo.getDescricao());
        doc.append("melhorMes", tempo.getMelhorMes());
        doc.append("justificativaMelhorMes", tempo.getJustificativaMelhorMes());

        List<Document> meses = new ArrayList<>();
        for (MesDaEstacao m : tempo.getMeses()) {
            meses.add(new Document("numero", m.numero())
                    .append("nome", m.nome())
                    .append("melhor", m.melhor())
                    .append("observacao", m.observacao()));
        }
        doc.append("meses", meses);
        return doc;
    }

    public static Tempo deDocumento(Document doc) {
        Tempo tempo = new Tempo(
                Estacao.valueOf(doc.getString("codigo")),
                doc.getString("descricao"),
                BsonUtil.lerInt(doc, "melhorMes"),
                doc.getString("justificativaMelhorMes"));
        tempo.setId(BsonUtil.lerId(doc));
        for (Document m : BsonUtil.lerSubdocumentos(doc, "meses")) {
            tempo.adicionarMes(BsonUtil.lerInt(m, "numero"), m.getString("observacao"));
        }
        return tempo;
    }
}
