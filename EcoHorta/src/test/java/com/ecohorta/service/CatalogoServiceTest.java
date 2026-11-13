package com.ecohorta.service;

import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.Estacao;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;
import com.ecohorta.model.Planta;
import com.ecohorta.model.RegaRecomendada;
import com.ecohorta.repository.FiltroPlantas;
import com.ecohorta.support.Ambiente;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogoServiceTest {

    private final Ambiente ambiente = new Ambiente();
    private final CatalogoService service = ambiente.servicos.catalogo();
    private final int total = CatalogoInicial.plantas().size();

    @Test
    void carregaCatalogoInicialSemDuplicar() {
        assertEquals(total, service.carregarCatalogoInicial());
        assertEquals(0, service.carregarCatalogoInicial());
        assertEquals((long) total, ambiente.plantas.contar());
    }

    @Test
    void carregaApenasAsPlantasQueFaltam() {
        service.cadastrar(new Planta("Tomate", "", 90, "", new RegaRecomendada(2, 500, "")));
        assertEquals(total - 1, service.carregarCatalogoInicial());
        assertEquals((long) total, ambiente.plantas.contar());
    }

    @Test
    void listaOrdenadaPorNomeIgnorandoAcentos() {
        service.carregarCatalogoInicial();
        List<String> nomes = service.listar().stream().map(Planta::getNomeBusca).toList();
        assertEquals(nomes.stream().sorted().toList(), nomes);
        assertEquals("Abacate", service.listar().get(0).getNome());
        assertEquals(total, nomes.size());
    }

    @Test
    void buscaPorNomeIgnorandoMaiusculasEAcentos() {
        service.carregarCatalogoInicial();
        assertEquals("Tomate", service.buscarPorNome("  tomate ").getNome());
        assertEquals("Rúcula", service.buscarPorNome("RUCULA").getNome());
        assertNotNull(service.buscarPorNome("tomate").getId());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorNome("Planta Inexistente"));
    }

    @Test
    void naoCadastraPlantaComNomeRepetido() {
        service.carregarCatalogoInicial();
        Planta repetida = new Planta("ALFACE", "", 30, "", new RegaRecomendada(1, 100, ""));
        assertThrows(RegraDeNegocioException.class, () -> service.cadastrar(repetida));
        Planta repetidaSemAcento = new Planta("Rucula", "", 30, "", new RegaRecomendada(1, 100, ""));
        assertThrows(RegraDeNegocioException.class, () -> service.cadastrar(repetidaSemAcento));
    }

    @Test
    void sugerePlantasParaOMes() {
        service.carregarCatalogoInicial();
        List<String> novembro = service.plantasParaPlantar(11).stream().map(Planta::getNome).toList();
        assertTrue(novembro.contains("Pimenta"));
        assertTrue(novembro.contains("Alface"));
        assertTrue(novembro.contains("Salsinha"));
        assertTrue(novembro.contains("Cebolinha"));
        assertFalse(novembro.contains("Tomate"));

        List<String> setembro = service.plantasParaPlantar(9).stream().map(Planta::getNome).toList();
        assertTrue(setembro.contains("Tomate"));
    }

    @Test
    void pesquisaPorParteDoNomeSemAcentos() {
        service.carregarCatalogoInicial();
        List<String> pimentas = service.pesquisar(FiltroPlantas.todas().comTermo("pimenta"))
                .stream().map(Planta::getNome).toList();
        assertTrue(pimentas.contains("Pimenta"));
        assertTrue(pimentas.contains("Pimenta-malagueta"));
        assertTrue(pimentas.contains("Pimenta-habanero"));
        assertTrue(service.pesquisar(FiltroPlantas.todas().comTermo("xyzxyz")).isEmpty());
        assertEquals("Rúcula", service.pesquisar(FiltroPlantas.todas().comTermo("rucula")).get(0).getNome());
    }

    @Test
    void filtraPorApartamentoELuzCombinados() {
        service.carregarCatalogoInicial();
        List<Planta> idealPoucaLuz = service.pesquisar(FiltroPlantas.todas()
                .comApartamento(NivelApartamento.IDEAL).comNivelLuz(NivelLuz.MINIMA));
        assertFalse(idealPoucaLuz.isEmpty());
        assertTrue(idealPoucaLuz.stream().allMatch(p -> p.getNivelLuz() == NivelLuz.MINIMA
                && p.getApartamento().nivel() == NivelApartamento.IDEAL));
        assertTrue(idealPoucaLuz.stream().anyMatch(p -> p.getNome().equals("Jiboia")));

        List<Planta> naoRecomendadas = service.pesquisar(
                FiltroPlantas.todas().comApartamento(NivelApartamento.NAO_RECOMENDADO));
        assertTrue(naoRecomendadas.stream().anyMatch(p -> p.getNome().equals("Abóbora-moranga")));
    }

    @Test
    void filtraPorEstacaoCategoriaEComestivel() {
        service.carregarCatalogoInicial();
        List<Planta> inverno = service.pesquisar(FiltroPlantas.todas().comEstacao(Estacao.INVERNO));
        assertTrue(inverno.stream().allMatch(p -> p.getEstacoes().contains(Estacao.INVERNO)));
        assertTrue(inverno.stream().anyMatch(p -> p.getNome().equals("Alho")));

        List<Planta> frutiferas = service.pesquisar(FiltroPlantas.todas().comCategoria(CategoriaPlanta.FRUTIFERA));
        assertTrue(frutiferas.size() >= 20);
        assertTrue(frutiferas.stream().allMatch(p -> p.getCategoria() == CategoriaPlanta.FRUTIFERA));

        List<Planta> naoComestiveis = service.pesquisar(FiltroPlantas.todas().comComestivel(false));
        assertTrue(naoComestiveis.stream().noneMatch(Planta::isComestivel));
        assertTrue(naoComestiveis.stream().anyMatch(p -> p.getNome().equals("Lírio")));
    }
}
