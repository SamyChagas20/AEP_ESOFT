package com.ecohorta.repository.mongo;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BsonUtilTest {

    @Test
    void converteDataIdaEVolta() {
        LocalDate dia = LocalDate.of(2026, 11, 10);
        Date date = BsonUtil.paraDate(dia);
        assertEquals(dia, BsonUtil.paraLocalDate(date));
    }

    @Test
    void trataNulosNasConversoes() {
        assertNull(BsonUtil.paraDate(null));
        assertNull(BsonUtil.paraLocalDate(null));
        assertNull(BsonUtil.paraObjectId(null));
        assertNull(BsonUtil.lerId(new Document()));
        assertNull(BsonUtil.lerReferencia(new Document(), "x"));
    }

    @Test
    void lerIdEReferenciaDevolvemTextoHexadecimal() {
        ObjectId id = new ObjectId();
        ObjectId ref = new ObjectId();
        Document doc = new Document("_id", id).append("hortaId", ref);
        assertEquals(id.toHexString(), BsonUtil.lerId(doc));
        assertEquals(ref.toHexString(), BsonUtil.lerReferencia(doc, "hortaId"));
        assertEquals(ref, BsonUtil.paraObjectId(ref.toHexString()));
    }

    @Test
    void lerSubdocumentosDevolveListaVaziaQuandoAusente() {
        assertTrue(BsonUtil.lerSubdocumentos(new Document(), "itens").isEmpty());
        Document doc = new Document("itens", List.of(new Document("a", 1)));
        assertEquals(1, BsonUtil.lerSubdocumentos(doc, "itens").size());
    }

    @Test
    void lerNumerosAceitaQualquerTipoNumericoEUsaZeroComoPadrao() {
        Document doc = new Document("i", 5).append("l", 7L).append("d", 2.5).append("x", "texto");
        assertEquals(5, BsonUtil.lerInt(doc, "i"));
        assertEquals(7, BsonUtil.lerInt(doc, "l"));
        assertEquals(0, BsonUtil.lerInt(doc, "x"));
        assertEquals(0, BsonUtil.lerInt(doc, "ausente"));
        assertEquals(2.5, BsonUtil.lerDouble(doc, "d"), 0.0001);
        assertEquals(5.0, BsonUtil.lerDouble(doc, "i"), 0.0001);
        assertEquals(0.0, BsonUtil.lerDouble(doc, "x"), 0.0001);
    }
}
