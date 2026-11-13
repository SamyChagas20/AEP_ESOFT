package com.ecohorta.service;

import com.ecohorta.model.AdaptacaoApartamento;
import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;
import com.ecohorta.model.Planta;
import com.ecohorta.model.RegaRecomendada;
import com.ecohorta.model.TipoCuidado;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lê o catálogo ampliado do arquivo {@code /catalogo/plantas.csv}.
 * Colunas: nome;científico;categoria;dias;luz;apartamento;regaDias;regaMl;janelas;dica
 * (a última coluna recebe o resto da linha, então a dica pode conter ponto e vírgula).
 */
public final class CatalogoCsv {

    public static final String RECURSO = "/catalogo/plantas.csv";

    private static final Map<String, CategoriaPlanta> CATEGORIAS = Map.ofEntries(
            Map.entry("FOL", CategoriaPlanta.FOLHOSA),
            Map.entry("FRU", CategoriaPlanta.HORTALICA_FRUTO),
            Map.entry("RAI", CategoriaPlanta.RAIZ_TUBERCULO),
            Map.entry("BUL", CategoriaPlanta.BULBO),
            Map.entry("ERV", CategoriaPlanta.ERVA_TEMPERO),
            Map.entry("LEG", CategoriaPlanta.LEGUMINOSA),
            Map.entry("FRT", CategoriaPlanta.FRUTIFERA),
            Map.entry("MED", CategoriaPlanta.MEDICINAL),
            Map.entry("PNC", CategoriaPlanta.PANC),
            Map.entry("FLO", CategoriaPlanta.FLOR),
            Map.entry("ORN", CategoriaPlanta.ORNAMENTAL));

    private CatalogoCsv() {
    }

    /** Carrega o recurso padrão do projeto. */
    public static List<Planta> carregar() {
        try (InputStream in = CatalogoCsv.class.getResourceAsStream(RECURSO)) {
            if (in == null) {
                throw new IllegalStateException("Arquivo do catálogo não encontrado: " + RECURSO);
            }
            return ler(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o catálogo: " + e.getMessage(), e);
        }
    }

    public static List<Planta> ler(BufferedReader leitor) throws IOException {
        List<Planta> plantas = new ArrayList<>();
        int numero = 0;
        String linha;
        while ((linha = leitor.readLine()) != null) {
            numero++;
            if (linha.isBlank() || linha.startsWith("#")) {
                continue;
            }
            try {
                plantas.add(converter(linha));
            } catch (RuntimeException e) {
                throw new IllegalStateException("Linha " + numero + " do catálogo é inválida (" + e.getMessage()
                        + "): " + linha, e);
            }
        }
        return plantas;
    }

    static Planta converter(String linha) {
        String[] c = linha.split(";", 10);
        if (c.length < 9) {
            throw new IllegalArgumentException("esperadas 10 colunas");
        }
        CategoriaPlanta categoria = CATEGORIAS.get(c[2].trim());
        if (categoria == null) {
            throw new IllegalArgumentException("categoria desconhecida: " + c[2]);
        }
        NivelLuz luz = switch (c[4].trim()) {
            case "B" -> NivelLuz.MINIMA;
            case "M" -> NivelLuz.MEDIA;
            case "P" -> NivelLuz.PLENA;
            default -> throw new IllegalArgumentException("luz desconhecida: " + c[4]);
        };
        NivelApartamento nivelApt = switch (c[5].trim()) {
            case "I" -> NivelApartamento.IDEAL;
            case "P" -> NivelApartamento.POSSIVEL;
            case "N" -> NivelApartamento.NAO_RECOMENDADO;
            default -> throw new IllegalArgumentException("apartamento desconhecido: " + c[5]);
        };
        int regaDias = Integer.parseInt(c[6].trim());
        String observacaoRega = regaDias >= 10
                ? "Deixar o substrato secar bem entre as regas"
                : "Regar quando a camada superficial do solo estiver seca";

        Planta planta = new Planta(c[0], c[1], Integer.parseInt(c[3].trim()),
                luz.getRotulo() + " — " + luz.getDescricao(),
                new RegaRecomendada(regaDias, Integer.parseInt(c[7].trim()), observacaoRega))
                .categoria(categoria)
                .nivelLuz(luz)
                .apartamento(new AdaptacaoApartamento(nivelApt, notaApartamento(nivelApt)))
                .comestivel(categoria != CategoriaPlanta.ORNAMENTAL);

        for (String janela : c[8].trim().split("\\|")) {
            String[] meses = janela.split("-");
            planta.adicionarJanela(Integer.parseInt(meses[0].trim()), Integer.parseInt(meses[1].trim()), "");
        }
        CuidadosPadrao.para(categoria).forEach(planta::adicionarCuidado);
        if (c.length == 10 && !c[9].isBlank()) {
            planta.adicionarCuidado(TipoCuidado.MANEJO, c[9].trim(), 0);
        }
        return planta;
    }

    private static String notaApartamento(NivelApartamento nivel) {
        return switch (nivel) {
            case IDEAL -> "Cresce bem em vasos dentro de casa, em janela ou bancada com a luz indicada";
            case POSSIVEL -> "Possível em vaso grande na varanda ou em janela bem iluminada";
            case NAO_RECOMENDADO -> "Exige muito espaço ou sol direto o dia todo; prefira quintal ou terreno";
        };
    }
}
