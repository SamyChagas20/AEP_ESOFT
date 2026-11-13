package com.ecohorta.service;

import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.Cuidado;
import com.ecohorta.model.TipoCuidado;

import java.util.List;

public final class CuidadosPadrao {

    private CuidadosPadrao() {
    }

    public static List<Cuidado> para(CategoriaPlanta categoria) {
        return switch (categoria) {
            case FOLHOSA -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubação orgânica rica em nitrogênio (húmus ou composto)", 15),
                    new Cuidado(TipoCuidado.PRAGAS, "Verificar lesmas, pulgões e lagartas nas folhas", 7),
                    new Cuidado(TipoCuidado.SOLO, "Solo rico em matéria orgânica, bem drenado e úmido", 0));
            case HORTALICA_FRUTO -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubar com composto ou NPK, reforçando o potássio na frutificação", 15),
                    new Cuidado(TipoCuidado.PRAGAS, "Inspecionar folhas e frutos (mosca-branca, ácaros, lagartas)", 7),
                    new Cuidado(TipoCuidado.SUSTENTACAO, "Usar tutor ou gaiola em variedades altas ou trepadeiras", 0));
            case RAIZ_TUBERCULO -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo rico em potássio e pobre em nitrogênio", 20),
                    new Cuidado(TipoCuidado.PRAGAS, "Observar folhas e raízes quanto a pragas e podridões", 10),
                    new Cuidado(TipoCuidado.SOLO, "Solo fofo e profundo, sem pedras", 0));
            case BULBO -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo orgânico e potássio a cada 20 dias", 20),
                    new Cuidado(TipoCuidado.SOLO, "Solo leve e bem drenado", 0),
                    new Cuidado(TipoCuidado.MANEJO, "Colher quando as folhas começarem a secar", 0));
            case ERVA_TEMPERO -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo orgânico leve a cada 30 dias", 30),
                    new Cuidado(TipoCuidado.PODA, "Podar as pontas para estimular a brotação e adiar a floração", 14),
                    new Cuidado(TipoCuidado.SOLO, "Solo leve e bem drenado; evitar encharcar", 0));
            case LEGUMINOSA -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo com fósforo e potássio; evitar excesso de nitrogênio", 20),
                    new Cuidado(TipoCuidado.PRAGAS, "Verificar vaquinhas, pulgões e fungos nas folhas", 10),
                    new Cuidado(TipoCuidado.SUSTENTACAO, "Usar tutor nas variedades trepadoras", 0));
            case FRUTIFERA -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubar com composto e potássio", 30),
                    new Cuidado(TipoCuidado.PRAGAS, "Verificar cochonilhas, moscas-das-frutas e fungos", 14),
                    new Cuidado(TipoCuidado.PODA, "Podar para formação e após a colheita", 0));
            case MEDICINAL -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo orgânico leve", 45),
                    new Cuidado(TipoCuidado.PODA, "Colher ou podar regularmente para estimular a rebrota", 21),
                    new Cuidado(TipoCuidado.SOLO, "Solo bem drenado", 0));
            case PANC -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo orgânico", 30),
                    new Cuidado(TipoCuidado.PODA, "Colher folhas e brotos com frequência para estimular a rebrota", 14),
                    new Cuidado(TipoCuidado.SOLO, "Solo rico em matéria orgânica", 0));
            case FLOR -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo orgânico para floração", 30),
                    new Cuidado(TipoCuidado.PODA, "Retirar as flores secas para estimular nova floração", 14),
                    new Cuidado(TipoCuidado.PRAGAS, "Verificar pulgões nos botões", 10));
            case ORNAMENTAL -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo líquido diluído para plantas de folhagem", 45),
                    new Cuidado(TipoCuidado.PODA, "Retirar folhas secas e limpar o pó das folhas", 30),
                    new Cuidado(TipoCuidado.LUMINOSIDADE, "Girar o vaso de vez em quando para crescer por igual", 0));
            case OUTRA -> List.of(
                    new Cuidado(TipoCuidado.ADUBACAO, "Adubo orgânico", 30));
        };
    }
}
