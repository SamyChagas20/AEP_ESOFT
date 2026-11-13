package com.ecohorta.service;

import com.ecohorta.model.Gravidade;
import com.ecohorta.model.ItemSaude;
import com.ecohorta.model.Planta;
import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.TipoRegistroSaude;
import com.ecohorta.model.Textos;
import com.ecohorta.support.Ambiente;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaudeServiceTest {

    private final Ambiente ambiente = new Ambiente();
    private final SaudeService service = ambiente.servicos.saude();

    private void carregarTudo() {
        ambiente.servicos.carregarDadosIniciais();
    }

    private Planta planta(String nome) {
        return ambiente.servicos.catalogo().buscarPorNome(nome);
    }

    private boolean contem(List<SaudeService.Alerta> alertas, String codigo) {
        return alertas.stream().anyMatch(a -> a.codigo().equals(codigo));
    }

    @Test
    void todasAsPlantasCitadasExistemNoCatalogo() {
        Set<String> catalogo = new HashSet<>();
        CatalogoInicial.plantas().forEach(p -> catalogo.add(p.getNomeBusca()));
        for (SaudeInicial.Definicao d : SaudeInicial.registros()) {
            for (SaudeInicial.Entrada e : d.evitar()) {
                assertTrue(catalogo.contains(Textos.normalizar(e.planta())), d.codigo() + " cita planta inexistente: " + e.planta());
            }
            for (SaudeInicial.Entrada e : d.recomendadas()) {
                assertTrue(catalogo.contains(Textos.normalizar(e.planta())), d.codigo() + " cita planta inexistente: " + e.planta());
            }
        }
    }

    @Test
    void nenhumaPlantaApareceDuasVezesNaMesmaLista() {
        for (SaudeInicial.Definicao d : SaudeInicial.registros()) {
            Set<String> evitar = new HashSet<>();
            d.evitar().forEach(e -> assertTrue(evitar.add(e.planta()), d.codigo() + " evitar repete " + e.planta()));
            Set<String> recomendadas = new HashSet<>();
            d.recomendadas().forEach(e -> assertTrue(recomendadas.add(e.planta()),
                    d.codigo() + " recomendadas repete " + e.planta()));

            evitar.forEach(n -> assertFalse(recomendadas.contains(n), d.codigo() + " contradiz-se em " + n));
        }
    }

    @Test
    void todoRegistroTemOrientacaoAvisoEGravidadeNosItensAEvitar() {
        for (SaudeInicial.Definicao d : SaudeInicial.registros()) {
            assertFalse(d.descricao().isBlank(), d.codigo());
            assertFalse(d.orientacao().isBlank(), d.codigo());
            assertFalse(d.aviso().isBlank(), d.codigo());
            assertFalse(d.evitar().isEmpty(), d.codigo() + " sem plantas a evitar");
            assertTrue(d.evitar().stream().allMatch(e -> e.gravidade() != null), d.codigo());
        }
    }

    @Test
    void cargaExigeOCatalogoCarregado() {
        assertThrows(RegraDeNegocioException.class, () -> service.carregarSaudeInicial());
    }

    @Test
    void carregaTrezeRegistrosSemDuplicar() {
        ambiente.servicos.catalogo().carregarCatalogoInicial();
        assertEquals(13, service.carregarSaudeInicial());
        assertEquals(0, service.carregarSaudeInicial());
        assertEquals(13, service.listar().size());
    }

    @Test
    void cargaInicialCompletaDevolveResumo() {
        Servicos.ResumoCarga carga = ambiente.servicos.carregarDadosIniciais();
        assertEquals(CatalogoInicial.plantas().size(), carga.plantas());
        assertEquals(4, carga.estacoes());
        assertEquals(13, carga.registrosSaude());
        Servicos.ResumoCarga segunda = ambiente.servicos.carregarDadosIniciais();
        assertEquals(0, segunda.plantas() + segunda.estacoes() + segunda.registrosSaude());
    }

    @Test
    void listaPorTipoEBuscaPorCodigo() {
        carregarTudo();
        assertEquals(2, service.listarPorTipo(TipoRegistroSaude.ANIMAL).size());
        assertEquals(1, service.listarPorTipo(TipoRegistroSaude.PESSOA).size());
        assertEquals(10, service.listarPorTipo(TipoRegistroSaude.COMORBIDADE).size());
        assertEquals("Gatos", service.buscarPorCodigo("gatos").getNome());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorCodigo("INEXISTENTE"));
    }

    @Test
    void itensGuardamReferenciaParaPlantaDoCatalogo() {
        carregarTudo();
        RegistroSaude gatos = service.buscarPorCodigo("GATOS");
        ItemSaude cebola = gatos.getEvitar().stream().filter(i -> i.plantaNome().equals("Cebola")).findFirst().orElseThrow();
        assertEquals(planta("Cebola").getId(), cebola.plantaId());
        assertEquals(Gravidade.GRAVE, cebola.gravidade());
    }

    @Test
    void cebolaEGravementeToxicaParaGatosECaesEHaCuidadoParaPessoasComRefluxo() {
        carregarTudo();
        SaudeService.PerfilSaude perfil = service.perfil(planta("Cebola"));

        assertTrue(contem(perfil.toxicidade(), "GATOS"));
        assertTrue(contem(perfil.toxicidade(), "CAES"));
        assertTrue(perfil.toxicidade().stream().filter(a -> a.codigo().equals("GATOS"))
                .allMatch(a -> a.item().gravidade() == Gravidade.GRAVE));
        assertTrue(contem(perfil.evitarEm(), "GASTRITE_REFLUXO"));
        assertFalse(perfil.semRegistros());
    }

    @Test
    void tomateTemAlertaParaPetsEPessoasPoisPartesVerdesSaoToxicas() {
        carregarTudo();
        SaudeService.PerfilSaude perfil = service.perfil(planta("Tomate"));

        assertTrue(contem(perfil.toxicidade(), "GATOS"));
        assertTrue(contem(perfil.toxicidade(), "CAES"));
        assertTrue(contem(perfil.toxicidade(), "PESSOAS"));
        assertTrue(contem(perfil.indicadaPara(), "HIPERTENSAO"));
        assertTrue(contem(perfil.evitarEm(), "DOENCA_RENAL"));
    }

    @Test
    void lirioEPerigosissimoParaGatosEUvaParaCaes() {
        carregarTudo();
        assertTrue(service.perfil(planta("Lírio")).toxicidade().stream()
                .anyMatch(a -> a.codigo().equals("GATOS") && a.item().gravidade() == Gravidade.GRAVE));
        assertTrue(service.perfil(planta("Uva")).toxicidade().stream()
                .anyMatch(a -> a.codigo().equals("CAES") && a.item().gravidade() == Gravidade.GRAVE));
        assertFalse(contem(service.perfil(planta("Uva")).toxicidade(), "GATOS"));
    }

    @Test
    void plantasSegurasParaPetsSaoIdentificadas() {
        carregarTudo();
        SaudeService.PerfilSaude perfil = service.perfil(planta("Manjericão"));
        assertEquals(2, perfil.seguraParaPets().size());
        assertTrue(perfil.toxicidade().isEmpty());
    }

    @Test
    void espinafreEvitadoNaDoencaRenalEIndicadoNoDiabetes() {
        carregarTudo();
        SaudeService.PerfilSaude perfil = service.perfil(planta("Espinafre"));
        assertTrue(perfil.evitarEm().stream().anyMatch(a -> a.codigo().equals("DOENCA_RENAL")
                && a.item().gravidade() == Gravidade.GRAVE));
        assertTrue(contem(perfil.evitarEm(), "ANTICOAGULANTE"));
        assertTrue(contem(perfil.indicadaPara(), "DIABETES"));
    }

    @Test
    void asmaEvitaPlantasComMuitoPolenEIndicaFolhagemLimpa() {
        carregarTudo();
        RegistroSaude respiratorio = service.buscarPorCodigo("RESPIRATORIO");
        List<String> evitar = respiratorio.getEvitar().stream().map(ItemSaude::plantaNome).toList();
        List<String> indicadas = respiratorio.getRecomendadas().stream().map(ItemSaude::plantaNome).toList();
        assertTrue(evitar.containsAll(List.of("Girassol", "Crisântemo", "Lavanda")));
        assertTrue(indicadas.containsAll(List.of("Jiboia", "Palmeira-areca", "Amor-perfeito")));
    }

    @Test
    void gravidezEvitaArrudaEPoejo() {
        carregarTudo();
        RegistroSaude gravidez = service.buscarPorCodigo("GRAVIDEZ_LACTACAO");
        assertTrue(gravidez.getEvitar().stream().anyMatch(i -> i.plantaNome().equals("Arruda")
                && i.gravidade() == Gravidade.GRAVE));
        assertTrue(gravidez.getEvitar().stream().anyMatch(i -> i.plantaNome().equals("Poejo")));
    }

    @Test
    void carambolaEGravePraQuemTemDoencaRenal() {
        carregarTudo();
        assertTrue(service.perfil(planta("Carambola")).evitarEm().stream()
                .anyMatch(a -> a.codigo().equals("DOENCA_RENAL") && a.item().gravidade() == Gravidade.GRAVE));
    }

    @Test
    void plantaSemRegistrosNaoTemAlertas() {
        carregarTudo();
        SaudeService.PerfilSaude perfil = service.perfil(planta("Tanchagem"));
        assertTrue(perfil.semRegistros());
        assertNotNull(perfil.toxicidade());
    }
}
