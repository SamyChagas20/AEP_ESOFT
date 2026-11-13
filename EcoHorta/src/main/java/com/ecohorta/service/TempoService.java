package com.ecohorta.service;

import com.ecohorta.model.Estacao;
import com.ecohorta.model.Planta;
import com.ecohorta.model.Tempo;
import com.ecohorta.repository.FiltroPlantas;
import com.ecohorta.repository.PlantaRepository;
import com.ecohorta.repository.TempoRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Estações do ano, melhores meses e relação com as plantas do catálogo. */
public class TempoService {

    private final TempoRepository tempos;
    private final PlantaRepository plantas;

    public TempoService(TempoRepository tempos, PlantaRepository plantas) {
        this.tempos = tempos;
        this.plantas = plantas;
    }

    /** Insere as estações que ainda não existem. @return quantas foram inseridas */
    public int carregarTempoInicial() {
        int inseridas = 0;
        for (Tempo tempo : TempoInicial.estacoes()) {
            if (tempos.buscarPorEstacao(tempo.getEstacao()).isEmpty()) {
                tempos.salvar(tempo);
                inseridas++;
            }
        }
        return inseridas;
    }

    public List<Tempo> listar() {
        return tempos.listar();
    }

    public Tempo buscar(Estacao estacao) {
        return tempos.buscarPorEstacao(estacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estação não cadastrada: " + estacao.getNome() + ". Carregue os dados iniciais."));
    }

    public Estacao estacaoDe(LocalDate data) {
        return Estacao.deMes(data.getMonthValue());
    }

    /** Plantas que têm janela de plantio na estação. */
    public List<Planta> plantasDaEstacao(Estacao estacao) {
        return plantas.filtrar(FiltroPlantas.todas().comEstacao(estacao));
    }

    /** Plantas com janela de plantio no melhor mês da estação. */
    public List<Planta> plantasDoMelhorMes(Estacao estacao) {
        int melhor = buscar(estacao).getMelhorMes();
        return plantasDaEstacao(estacao).stream().filter(p -> p.epocaAdequada(melhor)).toList();
    }

    /** Quantidade de plantas do catálogo que podem ser plantadas em cada mês da estação. */
    public Map<Integer, Long> plantasPorMes(Estacao estacao) {
        List<Planta> daEstacao = plantasDaEstacao(estacao);
        Map<Integer, Long> contagem = new LinkedHashMap<>();
        for (int mes : estacao.getMeses()) {
            contagem.put(mes, daEstacao.stream().filter(p -> p.epocaAdequada(mes)).count());
        }
        return contagem;
    }
}
