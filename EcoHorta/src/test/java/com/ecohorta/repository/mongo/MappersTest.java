package com.ecohorta.repository.mongo;

import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.Canteiro;
import com.ecohorta.model.Estacao;
import com.ecohorta.model.Gravidade;
import com.ecohorta.model.ItemSaude;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;
import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.Tempo;
import com.ecohorta.model.TipoRegistroSaude;
import com.ecohorta.model.Horta;
import com.ecohorta.model.Planta;
import com.ecohorta.model.Plantio;
import com.ecohorta.model.StatusPlantio;
import com.ecohorta.model.TipoCuidado;
import com.ecohorta.model.Usuario;
import com.ecohorta.service.CatalogoInicial;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MappersTest {

    @Test
    void usuarioFazIdaEVolta() {
        Usuario usuario = new Usuario("Ana", "ana@exemplo.com");
        usuario.setId(new ObjectId().toHexString());

        Document doc = UsuarioMapper.paraDocumento(usuario);
        assertEquals("ana@exemplo.com", doc.getString("email"));

        Usuario volta = UsuarioMapper.deDocumento(doc);
        assertEquals(usuario.getId(), volta.getId());
        assertEquals("Ana", volta.getNome());
        assertEquals("ana@exemplo.com", volta.getEmail());
    }

    @Test
    void documentoSemIdNaoTemCampoId() {
        Document doc = UsuarioMapper.paraDocumento(new Usuario("Ana", "ana@exemplo.com"));
        assertFalse(doc.containsKey("_id"));
        assertNull(UsuarioMapper.deDocumento(doc).getId());
    }

    @Test
    void plantaGravaDocumentoAninhadoEListasDeSubdocumentos() {
        Planta tomate = CatalogoInicial.tomate();
        tomate.setId(new ObjectId().toHexString());

        Document doc = PlantaMapper.paraDocumento(tomate);

        Document rega = doc.get("rega", Document.class);
        assertEquals(2, rega.getInteger("frequenciaDias"));
        assertEquals(1000, rega.getInteger("quantidadeMl"));

        List<Document> janelas = doc.getList("janelasPlantio", Document.class);
        assertEquals(2, janelas.size());
        assertEquals(8, janelas.get(0).getInteger("mesInicio"));

        List<Document> cuidados = doc.getList("cuidados", Document.class);
        assertEquals(4, cuidados.size());
        assertEquals("ADUBACAO", cuidados.get(0).getString("tipo"));
    }

    @Test
    void plantaFazIdaEVolta() {
        Planta alface = CatalogoInicial.alface();
        alface.setId(new ObjectId().toHexString());

        Planta volta = PlantaMapper.deDocumento(PlantaMapper.paraDocumento(alface));

        assertEquals(alface.getId(), volta.getId());
        assertEquals("Alface", volta.getNome());
        assertEquals("Lactuca sativa", volta.getNomeCientifico());
        assertEquals(45, volta.getDiasParaColheita());
        assertEquals(alface.getLuminosidade(), volta.getLuminosidade());
        assertEquals(alface.getRega(), volta.getRega());
        assertEquals(alface.getJanelasPlantio(), volta.getJanelasPlantio());
        assertEquals(alface.getCuidados(), volta.getCuidados());
        assertTrue(volta.epocaAdequada(1));
    }

    @Test
    void hortaGuardaReferenciaParaUsuarioECanteirosEmbutidos() {
        ObjectId usuarioId = new ObjectId();
        Horta horta = new Horta(usuarioId.toHexString(), "Quintal", "Maringá");
        horta.setId(new ObjectId().toHexString());
        horta.adicionarCanteiro(new Canteiro("Sol", 4.0, "Sol pleno"));
        horta.adicionarCanteiro(new Canteiro("Sombra", 2.5, "Meia sombra"));

        Document doc = HortaMapper.paraDocumento(horta);
        assertEquals(usuarioId, doc.getObjectId("usuarioId"));
        assertEquals(2, doc.getList("canteiros", Document.class).size());

        Horta volta = HortaMapper.deDocumento(doc);
        assertEquals(horta.getId(), volta.getId());
        assertEquals(usuarioId.toHexString(), volta.getUsuarioId());
        assertEquals(horta.getCanteiros(), volta.getCanteiros());
    }

    @Test
    void hortaSemCanteirosVoltaComListaVazia() {
        Document doc = HortaMapper.paraDocumento(new Horta(new ObjectId().toHexString(), "Vazia", ""));
        doc.remove("canteiros");
        assertTrue(HortaMapper.deDocumento(doc).getCanteiros().isEmpty());
    }

    @Test
    void plantioGuardaReferenciasDatasBsonEHistorico() {
        ObjectId hortaId = new ObjectId();
        ObjectId plantaId = new ObjectId();
        Plantio plantio = new Plantio(hortaId.toHexString(), plantaId.toHexString(), "Sol", 4,
                LocalDate.of(2026, 10, 1));
        plantio.setId(new ObjectId().toHexString());
        plantio.registrarRega(LocalDate.of(2026, 10, 3), 800, "manhã");
        plantio.registrarCuidado(LocalDate.of(2026, 10, 5), TipoCuidado.ADUBACAO, "NPK");

        Document doc = PlantioMapper.paraDocumento(plantio);
        assertEquals(hortaId, doc.getObjectId("hortaId"));
        assertEquals(plantaId, doc.getObjectId("plantaId"));
        assertTrue(doc.get("dataPlantio") instanceof Date);
        assertNull(doc.get("dataEncerramento"));
        assertEquals("ATIVO", doc.getString("status"));
        assertEquals(1, doc.getList("regas", Document.class).size());
        assertEquals(1, doc.getList("cuidados", Document.class).size());
    }

    @Test
    void plantioEncerradoFazIdaEVoltaCompleta() {
        Plantio plantio = new Plantio(new ObjectId().toHexString(), new ObjectId().toHexString(), "Sol", 4,
                LocalDate.of(2026, 10, 1));
        plantio.setId(new ObjectId().toHexString());
        plantio.registrarRega(LocalDate.of(2026, 10, 3), 800, "manhã");
        plantio.registrarCuidado(LocalDate.of(2026, 10, 5), TipoCuidado.PODA, "Desbrota");
        plantio.colher(LocalDate.of(2026, 12, 20));

        Plantio volta = PlantioMapper.deDocumento(PlantioMapper.paraDocumento(plantio));

        assertEquals(plantio.getId(), volta.getId());
        assertEquals(plantio.getHortaId(), volta.getHortaId());
        assertEquals(plantio.getPlantaId(), volta.getPlantaId());
        assertEquals("Sol", volta.getCanteiro());
        assertEquals(4, volta.getQuantidade());
        assertEquals(LocalDate.of(2026, 10, 1), volta.getDataPlantio());
        assertEquals(StatusPlantio.COLHIDO, volta.getStatus());
        assertEquals(LocalDate.of(2026, 12, 20), volta.getDataEncerramento());
        assertEquals(plantio.getRegas(), volta.getRegas());
        assertEquals(plantio.getCuidados(), volta.getCuidados());
    }

    @Test
    void plantaGravaLuzApartamentoEstacoesENomeDeBusca() {
        Planta rucula = com.ecohorta.service.CatalogoCsv.carregar().stream()
                .filter(p -> p.getNome().equals("Rúcula")).findFirst().orElseThrow();
        rucula.setId(new ObjectId().toHexString());

        Document doc = PlantaMapper.paraDocumento(rucula);

        assertEquals("rucula", doc.getString("nomeBusca"));
        assertEquals("FOLHOSA", doc.getString("categoria"));
        assertEquals("MEDIA", doc.getString("nivelLuz"));
        assertEquals(4, doc.getInteger("horasSolMinimas"));
        assertEquals(true, doc.get("comestivel"));
        assertEquals("IDEAL", doc.get("apartamento", Document.class).getString("nivel"));
        assertEquals(List.of("PRIMAVERA", "OUTONO", "INVERNO"), doc.getList("estacoes", String.class));
        assertEquals(6, doc.getInteger("melhorMesPlantio"));
    }

    @Test
    void plantaDoCsvFazIdaEVoltaComTodosOsCamposNovos() {
        Planta jiboia = com.ecohorta.service.CatalogoCsv.carregar().stream()
                .filter(p -> p.getNome().equals("Jiboia")).findFirst().orElseThrow();

        Planta volta = PlantaMapper.deDocumento(PlantaMapper.paraDocumento(jiboia));

        assertEquals(CategoriaPlanta.ORNAMENTAL, volta.getCategoria());
        assertEquals(NivelLuz.MINIMA, volta.getNivelLuz());
        assertEquals(NivelApartamento.IDEAL, volta.getApartamento().nivel());
        assertFalse(volta.isComestivel());
        assertEquals(jiboia.getApartamento(), volta.getApartamento());
        assertEquals(jiboia.getEstacoes(), volta.getEstacoes());
        assertTrue(PlantaMapper.paraDocumento(jiboia).get("melhorMesPlantio") == null);
    }

    @Test
    void documentoAntigoSemOsCamposNovosRecebeValoresPadrao() {
        Document antigo = PlantaMapper.paraDocumento(CatalogoInicial.alface());
        for (String campo : List.of("categoria", "nivelLuz", "comestivel", "apartamento", "nomeBusca")) {
            antigo.remove(campo);
        }

        Planta lida = PlantaMapper.deDocumento(antigo);

        assertEquals(CategoriaPlanta.OUTRA, lida.getCategoria());
        assertEquals(NivelLuz.PLENA, lida.getNivelLuz());
        assertEquals(NivelApartamento.NAO_RECOMENDADO, lida.getApartamento().nivel());
        assertTrue(lida.isComestivel());
        assertEquals("alface", lida.getNomeBusca());
    }

    @Test
    void tempoFazIdaEVoltaComMesesEmbutidos() {
        Tempo outono = com.ecohorta.service.TempoInicial.estacoes().get(2);
        outono.setId(new ObjectId().toHexString());

        Document doc = TempoMapper.paraDocumento(outono);
        assertEquals("OUTONO", doc.getString("codigo"));
        assertEquals(3, doc.getInteger("ordem"));
        assertEquals(3, doc.getList("meses", Document.class).size());

        Tempo volta = TempoMapper.deDocumento(doc);
        assertEquals(outono.getId(), volta.getId());
        assertEquals(Estacao.OUTONO, volta.getEstacao());
        assertEquals(4, volta.getMelhorMes());
        assertEquals(outono.getJustificativaMelhorMes(), volta.getJustificativaMelhorMes());
        assertEquals(outono.getMeses(), volta.getMeses());
        assertTrue(volta.getMeses().get(1).melhor());
    }

    @Test
    void saudeGuardaListasComReferenciasParaPlantas() {
        ObjectId cebola = new ObjectId();
        ObjectId manjericao = new ObjectId();
        RegistroSaude registro = new RegistroSaude("GATOS", TipoRegistroSaude.ANIMAL, "Gatos", "desc", "orient", "aviso")
                .adicionarEvitar(new ItemSaude(cebola.toHexString(), "Cebola", "anemia", Gravidade.GRAVE))
                .adicionarRecomendada(new ItemSaude(manjericao.toHexString(), "Manjericão", "seguro", null));
        registro.setId(new ObjectId().toHexString());

        Document doc = SaudeMapper.paraDocumento(registro);
        Document evitar = doc.getList("evitar", Document.class).get(0);
        assertEquals(cebola, evitar.getObjectId("plantaId"));
        assertEquals("GRAVE", evitar.getString("gravidade"));
        Document recomendada = doc.getList("recomendadas", Document.class).get(0);
        assertNull(recomendada.get("gravidade"));

        RegistroSaude volta = SaudeMapper.deDocumento(doc);
        assertEquals(registro.getId(), volta.getId());
        assertEquals("GATOS", volta.getCodigo());
        assertEquals(TipoRegistroSaude.ANIMAL, volta.getTipo());
        assertEquals(registro.getEvitar(), volta.getEvitar());
        assertEquals(registro.getRecomendadas(), volta.getRecomendadas());
        assertNull(volta.getRecomendadas().get(0).gravidade());
    }
}
