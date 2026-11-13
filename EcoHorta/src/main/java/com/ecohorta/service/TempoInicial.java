package com.ecohorta.service;

import com.ecohorta.model.Estacao;
import com.ecohorta.model.Tempo;

import java.util.List;

public final class TempoInicial {

    private TempoInicial() {
    }

    public static List<Tempo> estacoes() {
        return List.of(primavera(), verao(), outono(), inverno());
    }

    static Tempo primavera() {
        return new Tempo(Estacao.PRIMAVERA,
                "Temperaturas subindo e chuvas voltando. Fim do risco de geada na maior parte do Centro-Sul: "
                        + "estação principal para frutos, ervas de calor e leguminosas.",
                10,
                "Outubro reúne solo já aquecido, temperaturas amenas e chuvas regulares, sem o calor extremo do verão.")
                .adicionarMes(9, "Ainda pode haver frio e geada tardia; bom para sementeiras protegidas")
                .adicionarMes(10, "Clima ideal: temperatura amena, solo aquecido e chuva regular")
                .adicionarMes(11, "Calor crescente; reforçar a rega e cobrir o solo com palha");
    }

    static Tempo verao() {
        return new Tempo(Estacao.VERAO,
                "Calor e chuvas fortes. Favorece culturas de calor (quiabo, abóbora, manjericão), "
                        + "mas exige atenção a fungos, pragas e rega.",
                12,
                "Dezembro é o começo do verão: calor e chuva ajudam o desenvolvimento e dão tempo para o ciclo "
                        + "terminar antes do outono.")
                .adicionarMes(12, "Início do verão: melhor momento para culturas de calor")
                .adicionarMes(1, "Calor intenso e chuvas fortes; atenção a fungos e a pragas")
                .adicionarMes(2, "Fim do verão: bom para a segunda safra (feijão, tomate) e para preparar o outono");
    }

    static Tempo outono() {
        return new Tempo(Estacao.OUTONO,
                "Temperaturas amenas e menos pragas. Ótima época para folhosas, brássicas, raízes, alho e cebola.",
                4,
                "Abril tem temperaturas amenas, menos pragas e solo ainda morno: ideal para a maioria das hortaliças.")
                .adicionarMes(3, "Calor diminuindo; bom para começar couves, brócolis e folhosas")
                .adicionarMes(4, "Clima ideal para folhosas, raízes e ervas")
                .adicionarMes(5, "Primeiras friagens; época de alho, cebola, ervilha e fava");
    }

    static Tempo inverno() {
        return new Tempo(Estacao.INVERNO,
                "Dias curtos e frio, com risco de geada em regiões mais frias. Prefira culturas de clima fresco "
                        + "e proteja as sensíveis.",
                8,
                "Agosto marca o fim do inverno: o risco de geada cai, e já dá para preparar sementeiras da primavera.")
                .adicionarMes(6, "Geadas possíveis: cobrir as plantas sensíveis")
                .adicionarMes(7, "Mês mais frio: priorizar folhosas e brássicas resistentes")
                .adicionarMes(8, "Fim do inverno: preparar sementeiras de tomate, pimenta e berinjela");
    }
}
