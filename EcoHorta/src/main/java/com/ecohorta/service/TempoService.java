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

public class TempoService {

    private final TempoRepository tempos;
    private final PlantaRepository plantas;

    public TempoService(TempoRepository tempos, PlantaRepository plantas) {
        this.tempos = tempos;
        this.plantas = plantas;
    }

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

    public List<Planta> plantasDaEstacao(Estacao estacao) {
        return plantas.filtrar(FiltroPlantas.todas().comEstacao(estacao));
    }

    public List<Planta> plantasDoMelhorMes(Estacao estacao) {
        int melhor = buscar(estacao).getMelhorMes();
        return plantasDaEstacao(estacao).stream().filter(p -> p.epocaAdequada(melhor)).toList();
    }

    public Map<Integer, Long> plantasPorMes(Estacao estacao) {
        List<Planta> daEstacao = plantasDaEstacao(estacao);
        Map<Integer, Long> contagem = new LinkedHashMap<>();
        for (int mes : estacao.getMeses()) {
            contagem.put(mes, daEstacao.stream().filter(p -> p.epocaAdequada(mes)).count());
        }
        return contagem;
    }
}
