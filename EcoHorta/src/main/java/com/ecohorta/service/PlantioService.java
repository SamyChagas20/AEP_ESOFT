package com.ecohorta.service;

import com.ecohorta.model.Canteiro;
import com.ecohorta.model.Cuidado;
import com.ecohorta.model.Horta;
import com.ecohorta.model.Planta;
import com.ecohorta.model.Plantio;
import com.ecohorta.model.StatusPlantio;
import com.ecohorta.model.Tarefa;
import com.ecohorta.model.TipoCuidado;
import com.ecohorta.model.TipoTarefa;
import com.ecohorta.repository.HortaRepository;
import com.ecohorta.repository.PlantaRepository;
import com.ecohorta.repository.PlantioRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluxo principal do sistema: iniciar plantios, registrar regas e cuidados,
 * encerrar o ciclo (colheita/perda) e calcular as tarefas do dia.
 * Relaciona as coleções "hortas", "plantas" e "plantios".
 */
public class PlantioService {

    private final PlantioRepository plantios;
    private final HortaRepository hortas;
    private final PlantaRepository plantas;
    private final Clock relogio;

    public PlantioService(PlantioRepository plantios, HortaRepository hortas,
                          PlantaRepository plantas, Clock relogio) {
        this.plantios = plantios;
        this.hortas = hortas;
        this.plantas = plantas;
        this.relogio = relogio;
    }

    public Plantio iniciarPlantio(String hortaId, String nomePlanta, String nomeCanteiro,
                                  int quantidade, LocalDate dataPlantio) {
        Horta horta = hortas.buscarPorId(hortaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Horta não encontrada."));
        Planta planta = plantas.buscarPorNome(nomePlanta)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Planta não encontrada no catálogo: " + nomePlanta));
        Canteiro canteiro = horta.buscarCanteiro(nomeCanteiro)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Canteiro não encontrado na horta: " + nomeCanteiro));
        validarNaoFutura(dataPlantio, "A data de plantio");
        return plantios.salvar(new Plantio(horta.getId(), planta.getId(), canteiro.nome(), quantidade, dataPlantio));
    }

    /**
     * Registra uma rega.
     *
     * @param quantidadeMl quantidade usada; se nulo, usa a recomendada para a planta
     * @param data         data da rega; se nula, usa hoje
     */
    public Plantio registrarRega(String plantioId, Integer quantidadeMl, String observacao, LocalDate data) {
        Plantio plantio = buscarPlantio(plantioId);
        LocalDate dia = data == null ? hoje() : data;
        validarNaoFutura(dia, "A data da rega");
        int ml = quantidadeMl != null ? quantidadeMl : buscarPlanta(plantio.getPlantaId()).getRega().quantidadeMl();
        plantio.registrarRega(dia, ml, observacao);
        return plantios.salvar(plantio);
    }

    public Plantio registrarCuidado(String plantioId, TipoCuidado tipo, String descricao, LocalDate data) {
        Plantio plantio = buscarPlantio(plantioId);
        LocalDate dia = data == null ? hoje() : data;
        validarNaoFutura(dia, "A data do cuidado");
        plantio.registrarCuidado(dia, tipo, descricao);
        return plantios.salvar(plantio);
    }

    public Plantio colher(String plantioId, LocalDate data) {
        Plantio plantio = buscarPlantio(plantioId);
        plantio.colher(data == null ? hoje() : data);
        return plantios.salvar(plantio);
    }

    public Plantio marcarComoPerdido(String plantioId, LocalDate data) {
        Plantio plantio = buscarPlantio(plantioId);
        plantio.marcarComoPerdido(data == null ? hoje() : data);
        return plantios.salvar(plantio);
    }

    public List<Plantio> listarPorHorta(String hortaId) {
        return plantios.listarPorHorta(hortaId);
    }

    public Map<StatusPlantio, Long> resumo(String hortaId) {
        return plantios.contarPorStatus(hortaId);
    }

    public Planta buscarPlanta(String plantaId) {
        return plantas.buscarPorId(plantaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Planta não encontrada."));
    }

    /**
     * Calcula as tarefas pendentes (rega, cuidados periódicos e colheita) dos plantios
     * ativos da horta, até a data informada. Tarefas com data anterior vêm marcadas como atrasadas.
     */
    public List<Tarefa> tarefasDoDia(String hortaId, LocalDate hoje) {
        hortas.buscarPorId(hortaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Horta não encontrada."));

        Map<String, Planta> cache = new HashMap<>();
        List<Tarefa> tarefas = new ArrayList<>();

        for (Plantio plantio : plantios.listarPorHorta(hortaId)) {
            if (!plantio.isAtivo()) {
                continue;
            }
            Planta planta = cache.computeIfAbsent(plantio.getPlantaId(), this::buscarPlanta);

            LocalDate proximaRega = plantio.proximaRega(planta);
            if (!proximaRega.isAfter(hoje)) {
                String descricao = "Regar (cerca de " + planta.getRega().quantidadeMl() + " ml). "
                        + planta.getRega().observacao();
                tarefas.add(nova(TipoTarefa.REGA, plantio, planta, descricao.trim(), proximaRega, hoje));
            }

            for (Cuidado cuidado : planta.cuidadosPeriodicos()) {
                LocalDate proximo = plantio.proximoCuidado(cuidado);
                if (!proximo.isAfter(hoje)) {
                    String descricao = cuidado.tipo().getRotulo() + ": " + cuidado.descricao();
                    tarefas.add(nova(TipoTarefa.CUIDADO, plantio, planta, descricao, proximo, hoje));
                }
            }

            LocalDate colheita = plantio.dataPrevistaColheita(planta);
            if (planta.isComestivel() && !colheita.isAfter(hoje)) {
                tarefas.add(nova(TipoTarefa.COLHEITA, plantio, planta,
                        "Colheita prevista (" + planta.getDiasParaColheita() + " dias após o plantio)",
                        colheita, hoje));
            }
        }

        tarefas.sort(Comparator.comparing(Tarefa::dataPrevista)
                .thenComparing(Tarefa::plantaNome)
                .thenComparing(Tarefa::tipo));
        return tarefas;
    }

    private Tarefa nova(TipoTarefa tipo, Plantio plantio, Planta planta, String descricao,
                        LocalDate prevista, LocalDate hoje) {
        return new Tarefa(tipo, plantio.getId(), planta.getNome(), plantio.getCanteiro(),
                descricao, prevista, prevista.isBefore(hoje));
    }

    private Plantio buscarPlantio(String plantioId) {
        return plantios.buscarPorId(plantioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plantio não encontrado."));
    }

    private void validarNaoFutura(LocalDate data, String sujeito) {
        if (data.isAfter(hoje())) {
            throw new RegraDeNegocioException(sujeito + " não pode estar no futuro.");
        }
    }

    private LocalDate hoje() {
        return LocalDate.now(relogio);
    }
}
