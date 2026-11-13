package com.ecohorta.repository.mongo;

import com.ecohorta.model.AdaptacaoApartamento;
import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.Cuidado;
import com.ecohorta.model.Estacao;
import com.ecohorta.model.JanelaPlantio;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;
import com.ecohorta.model.Planta;
import com.ecohorta.model.RegaRecomendada;
import com.ecohorta.model.TipoCuidado;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public final class PlantaMapper {

    private PlantaMapper() {
    }

    public static Document paraDocumento(Planta planta) {
        Document doc = new Document();
        if (planta.getId() != null) {
            doc.put("_id", new ObjectId(planta.getId()));
        }
        doc.append("nome", planta.getNome());
        doc.append("nomeBusca", planta.getNomeBusca());
        doc.append("nomeCientifico", planta.getNomeCientifico());
        doc.append("categoria", planta.getCategoria().name());
        doc.append("comestivel", planta.isComestivel());
        doc.append("diasParaColheita", planta.getDiasParaColheita());

        doc.append("luminosidade", planta.getLuminosidade());
        doc.append("nivelLuz", planta.getNivelLuz().name());
        doc.append("horasSolMinimas", planta.getHorasSolMinimas());

        doc.append("apartamento", new Document("nivel", planta.getApartamento().nivel().name())
                .append("observacao", planta.getApartamento().observacao()));

        RegaRecomendada rega = planta.getRega();
        doc.append("rega", new Document("frequenciaDias", rega.frequenciaDias())
                .append("quantidadeMl", rega.quantidadeMl())
                .append("observacao", rega.observacao()));

        List<Document> janelas = new ArrayList<>();
        for (JanelaPlantio j : planta.getJanelasPlantio()) {
            janelas.add(new Document("mesInicio", j.mesInicio())
                    .append("mesFim", j.mesFim())
                    .append("observacao", j.observacao()));
        }
        doc.append("janelasPlantio", janelas);

        List<String> estacoes = new ArrayList<>();
        for (Estacao e : planta.getEstacoes()) {
            estacoes.add(e.name());
        }
        doc.append("estacoes", estacoes);
        doc.append("melhorMesPlantio", planta.getMelhorMesPlantio().isPresent()
                ? planta.getMelhorMesPlantio().getAsInt() : null);

        List<Document> cuidados = new ArrayList<>();
        for (Cuidado c : planta.getCuidados()) {
            cuidados.add(new Document("tipo", c.tipo().name())
                    .append("descricao", c.descricao())
                    .append("frequenciaDias", c.frequenciaDias()));
        }
        doc.append("cuidados", cuidados);
        return doc;
    }

    public static Planta deDocumento(Document doc) {
        Document r = doc.get("rega", Document.class);
        RegaRecomendada rega = new RegaRecomendada(
                BsonUtil.lerInt(r, "frequenciaDias"),
                BsonUtil.lerInt(r, "quantidadeMl"),
                r.getString("observacao"));

        Planta planta = new Planta(
                doc.getString("nome"),
                doc.getString("nomeCientifico"),
                BsonUtil.lerInt(doc, "diasParaColheita"),
                doc.getString("luminosidade"),
                rega);
        planta.setId(BsonUtil.lerId(doc));

        planta.categoria(BsonUtil.lerEnum(doc, "categoria", CategoriaPlanta.class, CategoriaPlanta.OUTRA));
        planta.nivelLuz(BsonUtil.lerEnum(doc, "nivelLuz", NivelLuz.class, NivelLuz.PLENA));
        Object comestivel = doc.get("comestivel");
        planta.comestivel(!(comestivel instanceof Boolean b) || b);

        Document apt = doc.get("apartamento", Document.class);
        if (apt != null) {
            planta.apartamento(new AdaptacaoApartamento(
                    BsonUtil.lerEnum(apt, "nivel", NivelApartamento.class, NivelApartamento.NAO_RECOMENDADO),
                    apt.getString("observacao")));
        }

        for (Document j : BsonUtil.lerSubdocumentos(doc, "janelasPlantio")) {
            planta.adicionarJanela(BsonUtil.lerInt(j, "mesInicio"), BsonUtil.lerInt(j, "mesFim"),
                    j.getString("observacao"));
        }
        for (Document c : BsonUtil.lerSubdocumentos(doc, "cuidados")) {
            planta.adicionarCuidado(TipoCuidado.valueOf(c.getString("tipo")),
                    c.getString("descricao"), BsonUtil.lerInt(c, "frequenciaDias"));
        }
        return planta;
    }
}
