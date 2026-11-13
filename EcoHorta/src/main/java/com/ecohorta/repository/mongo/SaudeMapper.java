package com.ecohorta.repository.mongo;

import com.ecohorta.model.Gravidade;
import com.ecohorta.model.ItemSaude;
import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.TipoRegistroSaude;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Converte RegistroSaude <-> Document (coleção "saude").
 * As listas "evitar" e "recomendadas" têm subdocumentos com "plantaId" (referência a "plantas").
 */
public final class SaudeMapper {

    private SaudeMapper() {
    }

    public static Document paraDocumento(RegistroSaude registro) {
        Document doc = new Document();
        if (registro.getId() != null) {
            doc.put("_id", new ObjectId(registro.getId()));
        }
        doc.append("codigo", registro.getCodigo());
        doc.append("tipo", registro.getTipo().name());
        doc.append("nome", registro.getNome());
        doc.append("descricao", registro.getDescricao());
        doc.append("orientacao", registro.getOrientacao());
        doc.append("aviso", registro.getAviso());
        doc.append("evitar", itens(registro.getEvitar()));
        doc.append("recomendadas", itens(registro.getRecomendadas()));
        return doc;
    }

    private static List<Document> itens(List<ItemSaude> itens) {
        List<Document> docs = new ArrayList<>();
        for (ItemSaude i : itens) {
            docs.add(new Document("plantaId", BsonUtil.paraObjectId(i.plantaId()))
                    .append("plantaNome", i.plantaNome())
                    .append("motivo", i.motivo())
                    .append("gravidade", i.gravidade() == null ? null : i.gravidade().name()));
        }
        return docs;
    }

    public static RegistroSaude deDocumento(Document doc) {
        RegistroSaude registro = new RegistroSaude(
                doc.getString("codigo"),
                TipoRegistroSaude.valueOf(doc.getString("tipo")),
                doc.getString("nome"),
                doc.getString("descricao"),
                doc.getString("orientacao"),
                doc.getString("aviso"));
        registro.setId(BsonUtil.lerId(doc));
        for (Document d : BsonUtil.lerSubdocumentos(doc, "evitar")) {
            registro.adicionarEvitar(item(d));
        }
        for (Document d : BsonUtil.lerSubdocumentos(doc, "recomendadas")) {
            registro.adicionarRecomendada(item(d));
        }
        return registro;
    }

    private static ItemSaude item(Document d) {
        return new ItemSaude(
                BsonUtil.lerReferencia(d, "plantaId"),
                d.getString("plantaNome"),
                d.getString("motivo"),
                BsonUtil.lerEnum(d, "gravidade", Gravidade.class, null));
    }
}
