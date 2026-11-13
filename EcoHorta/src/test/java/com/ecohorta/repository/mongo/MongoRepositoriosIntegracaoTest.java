package com.ecohorta.repository.mongo;

import com.ecohorta.config.MongoConnection;
import com.ecohorta.model.Estacao;
import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.ItemSaude;
import com.ecohorta.model.Gravidade;
import com.ecohorta.model.Horta;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;
import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.Tempo;
import com.ecohorta.model.TipoRegistroSaude;
import com.ecohorta.model.Planta;
import com.ecohorta.model.Plantio;
import com.ecohorta.model.StatusPlantio;
import com.ecohorta.model.TipoCuidado;
import com.ecohorta.model.Usuario;
import com.ecohorta.repository.FiltroPlantas;
import com.ecohorta.service.CatalogoInicial;
import com.ecohorta.service.Servicos;
import com.mongodb.MongoException;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class MongoRepositoriosIntegracaoTest {

    private static MongoConnection conexao;

    private MongoUsuarioRepository usuarios;
    private MongoHortaRepository hortas;
    private MongoPlantaRepository plantas;
    private MongoPlantioRepository plantios;
    private MongoTempoRepository tempos;
    private MongoSaudeRepository saude;

    @BeforeAll
    static void conectar() {
        String uri = System.getenv("MONGODB_URI");
        if (uri == null || uri.isBlank()) {
            uri = MongoConnection.URI_PADRAO;
        }
        MongoConnection candidata = null;
        try {
            candidata = new MongoConnection(uri, "ecohorta_teste_" + System.currentTimeMillis());
            candidata.verificarConexao();
            conexao = candidata;
        } catch (RuntimeException e) {
            if (candidata != null) {
                candidata.close();
            }
            if (Boolean.getBoolean("mongo.obrigatorio")) {
                throw new IllegalStateException("MongoDB obrigatório para validar a entrega: conexão indisponível.", e);
            }
        }
        assumeTrue(conexao != null, "MongoDB indisponível: testes de integração ignorados.");
    }

    @AfterAll
    static void encerrar() {
        if (conexao != null) {
            conexao.getBanco().drop();
            conexao.close();
        }
    }

    @BeforeEach
    void limparBanco() {
        conexao.getBanco().drop();
        MongoIndices.criar(conexao.getBanco());
        usuarios = new MongoUsuarioRepository(conexao.getBanco());
        hortas = new MongoHortaRepository(conexao.getBanco());
        plantas = new MongoPlantaRepository(conexao.getBanco());
        plantios = new MongoPlantioRepository(conexao.getBanco());
        tempos = new MongoTempoRepository(conexao.getBanco());
        saude = new MongoSaudeRepository(conexao.getBanco());
    }

    private Servicos servicos() {
        return Servicos.criar(usuarios, hortas, plantas, plantios, tempos, saude, java.time.Clock.systemUTC());
    }

    @Test
    void usuarioEhSalvoEBuscadoPorIdEEmail() {
        Usuario ana = usuarios.salvar(new Usuario("Ana", "ana@exemplo.com"));

        assertEquals("Ana", usuarios.buscarPorId(ana.getId()).orElseThrow().getNome());
        assertEquals(ana.getId(), usuarios.buscarPorEmail("ANA@exemplo.com").orElseThrow().getId());
        assertEquals(1, usuarios.listarTodos().size());
        assertTrue(usuarios.buscarPorId("id-invalido").isEmpty());
        assertTrue(usuarios.buscarPorId(null).isEmpty());
    }

    @Test
    void indiceUnicoImpedeEmailDuplicado() {
        usuarios.salvar(new Usuario("Ana", "ana@exemplo.com"));
        assertThrows(MongoException.class, () -> usuarios.salvar(new Usuario("Outra", "ana@exemplo.com")));
    }

    @Test
    void plantaPreservaDocumentosAninhadosEListas() {
        Planta salva = plantas.salvar(CatalogoInicial.tomate());

        Planta lida = plantas.buscarPorNome("TOMATE").orElseThrow();
        assertEquals(salva.getId(), lida.getId());
        assertEquals(salva.getRega(), lida.getRega());
        assertEquals(salva.getJanelasPlantio(), lida.getJanelasPlantio());
        assertEquals(salva.getCuidados(), lida.getCuidados());
        assertEquals(1L, plantas.contar());
        assertEquals(1, plantas.listarTodas().size());
        assertTrue(plantas.buscarPorNome("Jabuticaba").isEmpty());
        assertTrue(plantas.buscarPorNome(" ").isEmpty());
        assertTrue(plantas.buscarPorId(salva.getId()).isPresent());
    }

    @Test
    void salvarDuasVezesAtualizaOMesmoDocumento() {
        Planta planta = plantas.salvar(CatalogoInicial.alface());
        plantas.salvar(planta);
        assertEquals(1L, plantas.contar());
    }

    @Test
    void hortaGuardaCanteirosEmbutidosEReferenciaOUsuario() {
        Usuario ana = usuarios.salvar(new Usuario("Ana", "ana@exemplo.com"));
        Usuario bia = usuarios.salvar(new Usuario("Bia", "bia@exemplo.com"));
        Horta horta = new Horta(ana.getId(), "Quintal", "Maringá");
        horta.adicionarCanteiro(new com.ecohorta.model.Canteiro("Sol", 4.0, "Sol pleno"));
        hortas.salvar(horta);
        hortas.salvar(new Horta(bia.getId(), "Da Bia", ""));

        List<Horta> daAna = hortas.listarPorUsuario(ana.getId());
        assertEquals(1, daAna.size());
        assertEquals(1, daAna.get(0).getCanteiros().size());
        assertEquals(1, hortas.listarPorUsuario(bia.getId()).size());
        assertTrue(hortas.buscarPorId(horta.getId()).isPresent());
        assertTrue(hortas.listarPorUsuario("invalido").isEmpty());
        assertTrue(hortas.buscarPorId("invalido").isEmpty());
    }

    @Test
    void plantioRelacionaColecoesEFuncionaComAgregacaoELookup() {
        Usuario ana = usuarios.salvar(new Usuario("Ana", "ana@exemplo.com"));
        Horta horta = hortas.salvar(new Horta(ana.getId(), "Quintal", ""));
        Planta tomate = plantas.salvar(CatalogoInicial.tomate());
        Planta alface = plantas.salvar(CatalogoInicial.alface());

        Plantio p1 = plantios.salvar(new Plantio(horta.getId(), tomate.getId(), "Sol", 3, LocalDate.of(2026, 10, 1)));
        Plantio p2 = plantios.salvar(new Plantio(horta.getId(), alface.getId(), "Sol", 5, LocalDate.of(2026, 9, 1)));
        p1.registrarRega(LocalDate.of(2026, 10, 3), 800, "manhã");
        p1.registrarCuidado(LocalDate.of(2026, 10, 5), TipoCuidado.ADUBACAO, "NPK");
        plantios.salvar(p1);
        p2.colher(LocalDate.of(2026, 10, 20));
        plantios.salvar(p2);

        List<Plantio> lista = plantios.listarPorHorta(horta.getId());
        assertEquals(2, lista.size());
        assertEquals(p2.getId(), lista.get(0).getId());
        Plantio lido = plantios.buscarPorId(p1.getId()).orElseThrow();
        assertEquals(1, lido.getRegas().size());
        assertEquals(1, lido.getCuidados().size());

        Map<StatusPlantio, Long> resumo = plantios.contarPorStatus(horta.getId());
        assertEquals(1L, resumo.get(StatusPlantio.ATIVO));
        assertEquals(1L, resumo.get(StatusPlantio.COLHIDO));
        assertEquals(0L, resumo.get(StatusPlantio.PERDIDO));

        Document comPlanta = conexao.getBanco().getCollection(MongoPlantioRepository.COLECAO)
                .aggregate(List.of(
                        Aggregates.match(Filters.eq("_id", new ObjectId(p1.getId()))),
                        Aggregates.lookup(MongoPlantaRepository.COLECAO, "plantaId", "_id", "planta")))
                .first();
        assertEquals("Tomate", comPlanta.getList("planta", Document.class).get(0).getString("nome"));

        assertTrue(plantios.listarPorHorta("invalido").isEmpty());
        assertEquals(0L, plantios.contarPorStatus("invalido").get(StatusPlantio.ATIVO));
        assertTrue(plantios.buscarPorId("invalido").isEmpty());
    }

    @Test
    void cargaInicialCompletaEIdempotenteNoMongo() {
        Servicos servicos = servicos();
        Servicos.ResumoCarga primeira = servicos.carregarDadosIniciais();
        assertEquals(CatalogoInicial.plantas().size(), primeira.plantas());
        assertEquals(4, primeira.estacoes());
        assertEquals(13, primeira.registrosSaude());

        Servicos.ResumoCarga segunda = servicos.carregarDadosIniciais();
        assertEquals(0, segunda.plantas() + segunda.estacoes() + segunda.registrosSaude());
        assertTrue(plantas.contar() > 200);
    }

    @Test
    void filtrosUsamCamposAninhadosEListasDoDocumento() {
        servicos().catalogo().carregarCatalogoInicial();

        List<com.ecohorta.model.Planta> idealPoucaLuz = plantas.filtrar(FiltroPlantas.todas()
                .comApartamento(NivelApartamento.IDEAL).comNivelLuz(NivelLuz.MINIMA));
        assertTrue(idealPoucaLuz.stream().anyMatch(p -> p.getNome().equals("Jiboia")));
        assertTrue(idealPoucaLuz.stream().allMatch(p -> p.getNivelLuz() == NivelLuz.MINIMA));

        List<com.ecohorta.model.Planta> inverno = plantas.filtrar(FiltroPlantas.todas().comEstacao(Estacao.INVERNO));
        assertTrue(inverno.stream().anyMatch(p -> p.getNome().equals("Alho")));

        assertEquals("Rúcula", plantas.filtrar(FiltroPlantas.todas().comTermo("RUCULA")).get(0).getNome());
        assertEquals("Rúcula", plantas.buscarPorNome("rucula").orElseThrow().getNome());
        assertTrue(plantas.filtrar(FiltroPlantas.todas().comComestivel(false)).size() > 20);
    }

    @Test
    void indicesUnicosImpedemEstacaoESaudeDuplicadas() {
        Tempo primavera = com.ecohorta.service.TempoInicial.estacoes().get(0);
        tempos.salvar(primavera);
        Tempo duplicada = com.ecohorta.service.TempoInicial.estacoes().get(0);
        assertThrows(MongoException.class, () -> tempos.salvar(duplicada));

        saude.salvar(new RegistroSaude("GATOS", TipoRegistroSaude.ANIMAL, "Gatos", "", "", ""));
        assertThrows(MongoException.class,
                () -> saude.salvar(new RegistroSaude("GATOS", TipoRegistroSaude.ANIMAL, "Gatos 2", "", "", "")));
    }

    @Test
    void saudeEncontraRegistrosPelasListasEmbutidasEEstacoesLigamComPlantas() {
        servicos().carregarDadosIniciais();

        String idCebola = plantas.buscarPorNome("Cebola").orElseThrow().getId();
        List<RegistroSaude> citam = saude.listarPorPlanta(idCebola);
        assertTrue(citam.stream().anyMatch(r -> r.getCodigo().equals("GATOS")
                && r.getEvitar().stream().anyMatch(i -> i.gravidade() == Gravidade.GRAVE)));
        assertTrue(citam.stream().anyMatch(r -> r.getCodigo().equals("CAES")));
        assertTrue(saude.listarPorPlanta("invalido").isEmpty());

        assertEquals("Gatos", saude.buscarPorCodigo("gatos").orElseThrow().getNome());
        assertEquals(10, saude.listarPorTipo(TipoRegistroSaude.COMORBIDADE).size());
        assertEquals(13, saude.listar().size());
        assertTrue(saude.buscarPorCodigo(null).isEmpty());

        assertEquals(Estacao.PRIMAVERA, tempos.listar().get(0).getEstacao());
        assertEquals(10, tempos.buscarPorEstacao(Estacao.PRIMAVERA).orElseThrow().getMelhorMes());
        Document primaveraComPlantas = conexao.getBanco().getCollection(MongoTempoRepository.COLECAO)
                .aggregate(List.of(
                        Aggregates.match(Filters.eq("codigo", "PRIMAVERA")),
                        Aggregates.lookup(MongoPlantaRepository.COLECAO, "codigo", "estacoes", "plantas")))
                .first();
        assertTrue(primaveraComPlantas.getList("plantas", Document.class).size() > 50);
    }

    @Test
    void filtrosCombinadosExigemTodosOsCriterios() {
        servicos().catalogo().carregarCatalogoInicial();
        FiltroPlantas filtro = FiltroPlantas.todas().comTermo("RUCULA")
                .comCategoria(CategoriaPlanta.FOLHOSA).comEstacao(Estacao.INVERNO)
                .comNivelLuz(NivelLuz.MEDIA).comApartamento(NivelApartamento.IDEAL)
                .comComestivel(true);

        assertEquals(List.of("Rúcula", "Rúcula-selvagem"),
                plantas.filtrar(filtro).stream().map(Planta::getNome).toList());
        assertTrue(plantas.filtrar(filtro.comComestivel(false)).isEmpty());
        assertTrue(plantas.filtrar(filtro.comCategoria(CategoriaPlanta.ORNAMENTAL)).isEmpty());
    }

    @Test
    void buscaTrataMetacaracteresComoTextoLiteral() {
        servicos().catalogo().carregarCatalogoInicial();
        assertTrue(plantas.filtrar(FiltroPlantas.todas().comTermo(".*")).isEmpty());
        assertTrue(plantas.filtrar(FiltroPlantas.todas().comTermo("[")).isEmpty());
    }

    @Test
    void idsNulosInvalidosEValidosSemRegistroNaoEncontramDocumentos() {
        for (String id : java.util.Arrays.asList(null, "invalido", new ObjectId().toHexString())) {
            assertTrue(plantas.buscarPorId(id).isEmpty());
            assertTrue(usuarios.buscarPorId(id).isEmpty());
            assertTrue(hortas.buscarPorId(id).isEmpty());
            assertTrue(hortas.listarPorUsuario(id).isEmpty());
            assertTrue(plantios.buscarPorId(id).isEmpty());
            assertTrue(plantios.listarPorHorta(id).isEmpty());
            assertTrue(plantios.contarPorStatus(id).values().stream().allMatch(total -> total == 0));
            assertTrue(saude.listarPorPlanta(id).isEmpty());
        }
        assertTrue(tempos.buscarPorEstacao(Estacao.INVERNO).isEmpty());
        assertTrue(saude.buscarPorCodigo("INEXISTENTE").isEmpty());
    }

    @Test
    void salvarEstacaoExistenteAtualizaMesesSemDuplicar() {
        Tempo original = tempos.salvar(new Tempo(Estacao.VERAO, "Inicial", 12, "Justificativa"));
        Tempo revisado = new Tempo(Estacao.VERAO, "Revisado", 1, "Nova justificativa")
                .adicionarMes(1, "Observação revisada");
        revisado.setId(original.getId());
        tempos.salvar(revisado);

        Tempo lido = tempos.buscarPorEstacao(Estacao.VERAO).orElseThrow();
        assertEquals(1, tempos.listar().size());
        assertEquals(original.getId(), lido.getId());
        assertEquals("Revisado", lido.getDescricao());
        assertEquals(1, lido.getMelhorMes());
        assertEquals(revisado.getMeses(), lido.getMeses());
    }

    @Test
    void saudeConsultaRecomendadasEAtualizaListasSemDuplicar() {
        Planta alface = plantas.salvar(CatalogoInicial.alface());
        Planta tomate = plantas.salvar(CatalogoInicial.tomate());
        RegistroSaude registro = new RegistroSaude("TESTE", TipoRegistroSaude.COMORBIDADE,
                "Condição de teste", "", "", "");
        registro.adicionarRecomendada(new ItemSaude(alface.getId(), alface.getNome(), "Motivo de teste", null));
        saude.salvar(registro);

        assertEquals(List.of(registro.getId()), saude.listarPorPlanta(alface.getId())
                .stream().map(RegistroSaude::getId).toList());
        assertTrue(saude.listarPorPlanta(tomate.getId()).isEmpty());

        registro.adicionarEvitar(new ItemSaude(tomate.getId(), tomate.getNome(), "Outro motivo", Gravidade.LEVE));
        saude.salvar(registro);
        RegistroSaude lido = saude.buscarPorCodigo(" teste ").orElseThrow();
        assertEquals(1, saude.listar().size());
        assertEquals(registro.getRecomendadas(), lido.getRecomendadas());
        assertEquals(registro.getEvitar(), lido.getEvitar());
        assertEquals(registro.getId(), saude.listarPorPlanta(tomate.getId()).get(0).getId());
    }

    @Test
    void indiceDeNomeImpedeDuplicataMesmoSemAcento() {
        Planta original = CatalogoInicial.alface();
        plantas.salvar(new Planta("Rúcula", "", 30, "", original.getRega()));
        assertThrows(MongoException.class,
                () -> plantas.salvar(new Planta("RUCULA", "", 30, "", original.getRega())));
        assertEquals(1L, plantas.contar());
    }
}
