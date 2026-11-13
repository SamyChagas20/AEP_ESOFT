package com.ecohorta.service;

import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.Estacao;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;
import com.ecohorta.model.Planta;
import com.ecohorta.model.Textos;
import com.ecohorta.model.TipoCuidado;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Garante a qualidade do arquivo de dados do catálogo. */
class CatalogoCsvTest {

    private final List<Planta> csv = CatalogoCsv.carregar();

    @Test
    void trazMaisDe200PlantasNovas() {
        assertTrue(csv.size() > 200, "plantas no CSV: " + csv.size());
    }

    @Test
    void nomesSaoUnicosMesmoSemAcentosEIgnorandoAsCincoDetalhadas() {
        Set<String> vistos = new HashSet<>();
        for (Planta p : CatalogoInicial.plantas()) {
            assertTrue(vistos.add(p.getNomeBusca()), "nome repetido: " + p.getNome());
        }
    }

    @Test
    void todasTemDadosEssenciais() {
        for (Planta p : csv) {
            assertFalse(p.getNomeCientifico().isEmpty(), p.getNome() + ": científico");
            assertFalse(p.getJanelasPlantio().isEmpty(), p.getNome() + ": janelas");
            assertFalse(p.getCuidados().isEmpty(), p.getNome() + ": cuidados");
            assertFalse(p.getEstacoes().isEmpty(), p.getNome() + ": estações");
            assertFalse(p.getApartamento().observacao().isEmpty(), p.getNome() + ": apartamento");
            assertTrue(p.getHorasSolMinimas() >= 2, p.getNome() + ": horas de sol");
        }
    }

    @Test
    void cobreTodasAsCategoriasNiveisDeLuzEDeApartamento() {
        for (CategoriaPlanta c : CategoriaPlanta.values()) {
            if (c != CategoriaPlanta.OUTRA) {
                assertTrue(csv.stream().anyMatch(p -> p.getCategoria() == c), "sem plantas em " + c);
            }
        }
        for (NivelLuz n : NivelLuz.values()) {
            assertTrue(csv.stream().anyMatch(p -> p.getNivelLuz() == n), "sem plantas com luz " + n);
        }
        for (NivelApartamento n : NivelApartamento.values()) {
            assertTrue(csv.stream().anyMatch(p -> p.getApartamento().nivel() == n), "sem plantas " + n);
        }
    }

    @Test
    void todasAsEstacoesTemPlantas() {
        for (Estacao e : Estacao.values()) {
            assertTrue(csv.stream().anyMatch(p -> p.getEstacoes().contains(e)), "sem plantas em " + e);
        }
    }

    @Test
    void ornamentaisNaoSaoComestiveis() {
        assertTrue(csv.stream().filter(p -> p.getCategoria() == CategoriaPlanta.ORNAMENTAL)
                .noneMatch(Planta::isComestivel));
        assertTrue(csv.stream().filter(p -> p.getCategoria() != CategoriaPlanta.ORNAMENTAL)
                .allMatch(Planta::isComestivel));
    }

    @Test
    void plantaDoCsvTemCuidadosPadraoEDicaComoManejo() throws IOException {
        Planta rucula = csv.stream().filter(p -> p.getNome().equals("Rúcula")).findFirst().orElseThrow();
        assertEquals(CategoriaPlanta.FOLHOSA, rucula.getCategoria());
        assertEquals(NivelLuz.MEDIA, rucula.getNivelLuz());
        assertEquals(NivelApartamento.IDEAL, rucula.getApartamento().nivel());
        assertTrue(rucula.getCuidados().stream().anyMatch(c -> c.tipo() == TipoCuidado.ADUBACAO && c.periodico()));
        assertTrue(rucula.getCuidados().stream().anyMatch(c -> c.tipo() == TipoCuidado.MANEJO
                && c.descricao().contains("no calor sobe para flor"))); // dica com ponto e vírgula preservada
        assertEquals(Textos.normalizar("Rúcula"), rucula.getNomeBusca());
    }

    @Test
    void suculentasTemRegaEspacadaEObservacaoPropria() {
        Planta babosa = csv.stream().filter(p -> p.getNome().equals("Babosa")).findFirst().orElseThrow();
        assertEquals(14, babosa.getRega().frequenciaDias());
        assertTrue(babosa.getRega().observacao().contains("secar"));
    }

    @Test
    void leitorIgnoraComentariosELinhasEmBranco() throws IOException {
        String texto = "# comentário\n\nTeste;Testus plantus;FOL;30;B;I;2;100;1-12;dica\n";
        List<Planta> plantas = CatalogoCsv.ler(new BufferedReader(new StringReader(texto)));
        assertEquals(1, plantas.size());
        assertEquals(NivelLuz.MINIMA, plantas.get(0).getNivelLuz());
    }

    @Test
    void leitorSemDicaNaoCriaCuidadoDeManejoExtra() throws IOException {
        List<Planta> plantas = CatalogoCsv.ler(new BufferedReader(new StringReader(
                "Teste;Testus;ERV;30;P;N;2;100;3-5|9-10;")));
        assertEquals(2, plantas.get(0).getJanelasPlantio().size());
        assertEquals(3, plantas.get(0).getCuidados().size());
    }

    @Test
    void linhasInvalidasInformamONumeroDaLinha() {
        assertThrows(IllegalStateException.class, () -> CatalogoCsv.ler(new BufferedReader(new StringReader("so;duas"))));
        assertThrows(IllegalStateException.class, () -> CatalogoCsv.ler(new BufferedReader(new StringReader(
                "X;Y;ZZZ;30;P;N;2;100;3-5;"))));
        assertThrows(IllegalStateException.class, () -> CatalogoCsv.ler(new BufferedReader(new StringReader(
                "X;Y;FOL;30;Q;N;2;100;3-5;"))));
        assertThrows(IllegalStateException.class, () -> CatalogoCsv.ler(new BufferedReader(new StringReader(
                "X;Y;FOL;30;P;Z;2;100;3-5;"))));
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> CatalogoCsv.ler(new BufferedReader(new StringReader("# ok\nX;Y;FOL;trinta;P;N;2;100;3-5;"))));
        assertTrue(e.getMessage().contains("Linha 2"));
    }
}
