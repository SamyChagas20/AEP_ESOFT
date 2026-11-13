package com.ecohorta.repository.mongo;

import com.ecohorta.model.Usuario;
import org.bson.Document;
import org.bson.types.ObjectId;

/** Converte Usuario <-> Document (coleção "usuarios"). */
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Document paraDocumento(Usuario usuario) {
        Document doc = new Document();
        if (usuario.getId() != null) {
            doc.put("_id", new ObjectId(usuario.getId()));
        }
        doc.append("nome", usuario.getNome());
        doc.append("email", usuario.getEmail());
        return doc;
    }

    public static Usuario deDocumento(Document doc) {
        Usuario usuario = new Usuario(doc.getString("nome"), doc.getString("email"));
        usuario.setId(BsonUtil.lerId(doc));
        return usuario;
    }
}
