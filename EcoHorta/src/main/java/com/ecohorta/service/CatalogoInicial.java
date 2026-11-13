package com.ecohorta.service;

import com.ecohorta.model.AdaptacaoApartamento;
import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;
import com.ecohorta.model.Planta;
import com.ecohorta.model.RegaRecomendada;
import com.ecohorta.model.TipoCuidado;

import java.util.ArrayList;
import java.util.List;

public final class CatalogoInicial {

    private CatalogoInicial() {
    }

    public static List<Planta> plantas() {
        List<Planta> todas = new ArrayList<>(detalhadas());
        todas.addAll(CatalogoCsv.carregar());
        return todas;
    }

    public static List<Planta> detalhadas() {
        return List.of(tomate(), cebolinha(), pimenta(), alface(), salsinha());
    }

    private static AdaptacaoApartamento apt(NivelApartamento nivel, String nota) {
        return new AdaptacaoApartamento(nivel, nota);
    }

    public static Planta tomate() {
        return new Planta("Tomate", "Solanum lycopersicum", 90, "Sol pleno (mínimo 6 h por dia)",
                new RegaRecomendada(2, 1000, "Regar na base da planta, sem molhar folhas e frutos"))
                .categoria(CategoriaPlanta.HORTALICA_FRUTO).nivelLuz(NivelLuz.PLENA)
                .apartamento(apt(NivelApartamento.POSSIVEL, "Possível em vaso grande (mín. 20 L) em varanda com sol o dia todo; prefira tomate-cereja"))
                .adicionarJanela(8, 10, "Plantio principal, fim do inverno e primavera")
                .adicionarJanela(2, 3, "Safrinha, onde o inverno é ameno")
                .adicionarCuidado(TipoCuidado.ADUBACAO, "Adubar com composto ou NPK a cada 15 dias", 15)
                .adicionarCuidado(TipoCuidado.SUSTENTACAO, "Amarrar a haste ao tutor conforme cresce", 7)
                .adicionarCuidado(TipoCuidado.PODA, "Remover os brotos laterais (desbrota)", 7)
                .adicionarCuidado(TipoCuidado.PRAGAS, "Inspecionar folhas (mosca-branca, traça, ácaros)", 7);
    }

    public static Planta cebolinha() {
        return new Planta("Cebolinha", "Allium fistulosum", 60, "Sol pleno a meia sombra",
                new RegaRecomendada(1, 200, "Manter o solo sempre úmido"))
                .categoria(CategoriaPlanta.ERVA_TEMPERO).nivelLuz(NivelLuz.MEDIA)
                .apartamento(apt(NivelApartamento.IDEAL, "Ótima em vasos na janela ou cozinha com sol da manhã"))
                .adicionarJanela(1, 12, "Pode ser plantada o ano todo")
                .adicionarCuidado(TipoCuidado.ADUBACAO, "Adubo orgânico (húmus) a cada 30 dias", 30)
                .adicionarCuidado(TipoCuidado.MANEJO, "Colher cortando a cerca de 2 cm do solo para rebrotar", 0)
                .adicionarCuidado(TipoCuidado.SOLO, "Solo fofo e rico em matéria orgânica", 0);
    }

    public static Planta pimenta() {
        return new Planta("Pimenta", "Capsicum spp.", 100, "Sol pleno",
                new RegaRecomendada(2, 500, "Evitar encharcamento do solo"))
                .categoria(CategoriaPlanta.HORTALICA_FRUTO).nivelLuz(NivelLuz.PLENA)
                .apartamento(apt(NivelApartamento.POSSIVEL, "Possível em vaso na varanda ou janela com sol forte"))
                .adicionarJanela(9, 11, "Primavera")
                .adicionarCuidado(TipoCuidado.ADUBACAO, "Adubo rico em potássio na fase de frutificação", 20)
                .adicionarCuidado(TipoCuidado.PRAGAS, "Verificar pulgões e ácaros nas folhas", 10)
                .adicionarCuidado(TipoCuidado.SUSTENTACAO, "Tutorar plantas altas ou carregadas de frutos", 0);
    }

    public static Planta alface() {
        return new Planta("Alface", "Lactuca sativa", 45, "Sol pleno em clima ameno; meia sombra no calor",
                new RegaRecomendada(1, 250, "Regas leves e frequentes"))
                .categoria(CategoriaPlanta.FOLHOSA).nivelLuz(NivelLuz.MEDIA)
                .apartamento(apt(NivelApartamento.IDEAL, "Cresce bem em jardineiras ou vasos largos na janela"))
                .adicionarJanela(3, 8, "Melhor época, clima ameno")
                .adicionarJanela(9, 2, "Verão: usar variedades resistentes ao calor e meia sombra")
                .adicionarCuidado(TipoCuidado.ADUBACAO, "Adubação orgânica rica em nitrogênio", 15)
                .adicionarCuidado(TipoCuidado.PRAGAS, "Verificar lesmas e caracóis", 7)
                .adicionarCuidado(TipoCuidado.SOLO, "Solo rico em matéria orgânica e bem drenado", 0);
    }

    public static Planta salsinha() {
        return new Planta("Salsinha", "Petroselinum crispum", 70, "Meia sombra",
                new RegaRecomendada(2, 300, "Solo úmido, mas nunca encharcado"))
                .categoria(CategoriaPlanta.ERVA_TEMPERO).nivelLuz(NivelLuz.MEDIA)
                .apartamento(apt(NivelApartamento.IDEAL, "Ótima em vaso na janela com luz da manhã"))
                .adicionarJanela(3, 10, "Melhor época")
                .adicionarJanela(11, 2, "Verão: sombrear nas horas mais quentes")
                .adicionarCuidado(TipoCuidado.ADUBACAO, "Adubo orgânico a cada 30 dias", 30)
                .adicionarCuidado(TipoCuidado.MANEJO, "Colher as folhas externas, preservando o miolo", 0)
                .adicionarCuidado(TipoCuidado.LUMINOSIDADE, "Prefere meia sombra", 0);
    }
}
