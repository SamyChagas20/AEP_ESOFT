package com.ecohorta.repository.mongo;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/** Conversões auxiliares entre tipos Java e tipos BSON. */
public final class BsonUtil {

    private BsonUtil() {
    }

    /** LocalDate -> Date BSON (meia-noite UTC), permitindo consultas por data no MongoDB. */
    public static Date paraDate(LocalDate data) {
        return data == null ? null : Date.from(data.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    public static LocalDate paraLocalDate(Date data) {
        return data == null ? null : data.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }

    public static ObjectId paraObjectId(String hex) {
        return hex == null ? null : new ObjectId(hex);
    }

    /** Lê o "_id" do documento como texto hexadecimal. */
    public static String lerId(Document doc) {
        ObjectId id = doc.getObjectId("_id");
        return id == null ? null : id.toHexString();
    }

    /** Referência (ObjectId) gravada em outra coleção, devolvida como texto. */
    public static String lerReferencia(Document doc, String campo) {
        ObjectId ref = doc.getObjectId(campo);
        return ref == null ? null : ref.toHexString();
    }

    /** Lista de subdocumentos; devolve lista vazia se o campo não existir. */
    public static List<Document> lerSubdocumentos(Document doc, String campo) {
        List<Document> lista = doc.getList(campo, Document.class);
        return lista == null ? List.of() : lista;
    }

    public static double lerDouble(Document doc, String campo) {
        Object valor = doc.get(campo);
        return valor instanceof Number n ? n.doubleValue() : 0.0;
    }

    public static int lerInt(Document doc, String campo) {
        Object valor = doc.get(campo);
        return valor instanceof Number n ? n.intValue() : 0;
    }

    /** Lê um enum gravado como texto; usa o padrão se o campo faltar. */
    public static <E extends Enum<E>> E lerEnum(Document doc, String campo, Class<E> tipo, E padrao) {
        String valor = doc.getString(campo);
        return valor == null ? padrao : Enum.valueOf(tipo, valor);
    }
}
