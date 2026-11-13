package com.ecohorta.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Uma planta (ou lote de plantas) efetivamente cultivada em um canteiro.
 * Liga "hortas" e "plantas" por referência de id e guarda o histórico de
 * regas e cuidados como listas de subdocumentos.
 */
public class Plantio {

    private String id;
    private final String hortaId;
    private final String plantaId;
    private final String canteiro;
    private final int quantidade;
    private final LocalDate dataPlantio;
    private StatusPlantio status = StatusPlantio.ATIVO;
    private LocalDate dataEncerramento;
    private final List<RegistroRega> regas = new ArrayList<>();
    private final List<RegistroCuidado> cuidados = new ArrayList<>();

    public Plantio(String hortaId, String plantaId, String canteiro, int quantidade, LocalDate dataPlantio) {
        if (hortaId == null || hortaId.isBlank() || plantaId == null || plantaId.isBlank()) {
            throw new IllegalArgumentException("O plantio precisa estar ligado a uma horta e a uma planta.");
        }
        if (canteiro == null || canteiro.isBlank()) {
            throw new IllegalArgumentException("O canteiro é obrigatório.");
        }
        if (quantidade < 1) {
            throw new IllegalArgumentException("A quantidade deve ser de pelo menos 1.");
        }
        this.hortaId = hortaId;
        this.plantaId = plantaId;
        this.canteiro = canteiro.trim();
        this.quantidade = quantidade;
        this.dataPlantio = Objects.requireNonNull(dataPlantio, "A data do plantio é obrigatória.");
    }

    /** Reconstrói um plantio a partir do banco de dados, sem aplicar regras de transição. */
    public static Plantio restaurar(String id, String hortaId, String plantaId, String canteiro,
                                    int quantidade, LocalDate dataPlantio, StatusPlantio status,
                                    LocalDate dataEncerramento, List<RegistroRega> regas,
                                    List<RegistroCuidado> cuidados) {
        Plantio p = new Plantio(hortaId, plantaId, canteiro, quantidade, dataPlantio);
        p.id = id;
        p.status = status == null ? StatusPlantio.ATIVO : status;
        p.dataEncerramento = dataEncerramento;
        p.regas.addAll(regas);
        p.cuidados.addAll(cuidados);
        return p;
    }

    // ---------- comportamentos ----------

    public RegistroRega registrarRega(LocalDate data, int quantidadeMl, String observacao) {
        exigirAtivo();
        validarData(data, "A rega");
        RegistroRega registro = new RegistroRega(data, quantidadeMl, observacao);
        regas.add(registro);
        return registro;
    }

    public RegistroCuidado registrarCuidado(LocalDate data, TipoCuidado tipo, String descricao) {
        exigirAtivo();
        validarData(data, "O cuidado");
        RegistroCuidado registro = new RegistroCuidado(data, tipo, descricao);
        cuidados.add(registro);
        return registro;
    }

    public void colher(LocalDate data) {
        encerrar(StatusPlantio.COLHIDO, data);
    }

    public void marcarComoPerdido(LocalDate data) {
        encerrar(StatusPlantio.PERDIDO, data);
    }

    private void encerrar(StatusPlantio novoStatus, LocalDate data) {
        exigirAtivo();
        validarData(data, "O encerramento");
        this.status = novoStatus;
        this.dataEncerramento = data;
    }

    private void exigirAtivo() {
        if (status != StatusPlantio.ATIVO) {
            throw new IllegalStateException("Este plantio já foi encerrado (" + status.getRotulo() + ").");
        }
    }

    private void validarData(LocalDate data, String sujeito) {
        if (data == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
        if (data.isBefore(dataPlantio)) {
            throw new IllegalArgumentException(sujeito + " não pode ser anterior à data do plantio.");
        }
    }

    // ---------- cálculos de calendário ----------

    public boolean isAtivo() {
        return status == StatusPlantio.ATIVO;
    }

    public Optional<LocalDate> ultimaRega() {
        return regas.stream().map(RegistroRega::data).max(Comparator.naturalOrder());
    }

    /**
     * Data da próxima rega: última rega + frequência da planta.
     * Se ainda não foi regado, a primeira rega é devida já na data do plantio.
     */
    public LocalDate proximaRega(Planta planta) {
        return ultimaRega()
                .map(ultima -> ultima.plusDays(planta.getRega().frequenciaDias()))
                .orElse(dataPlantio);
    }

    public Optional<LocalDate> ultimoCuidado(TipoCuidado tipo) {
        return cuidados.stream()
                .filter(c -> c.tipo() == tipo)
                .map(RegistroCuidado::data)
                .max(Comparator.naturalOrder());
    }

    /** Data do próximo cuidado periódico (último do mesmo tipo, ou o plantio, + frequência). */
    public LocalDate proximoCuidado(Cuidado cuidado) {
        LocalDate base = ultimoCuidado(cuidado.tipo()).orElse(dataPlantio);
        return base.plusDays(cuidado.frequenciaDias());
    }

    public LocalDate dataPrevistaColheita(Planta planta) {
        return dataPlantio.plusDays(planta.getDiasParaColheita());
    }

    public long diasDesdePlantio(LocalDate hoje) {
        return ChronoUnit.DAYS.between(dataPlantio, hoje);
    }

    // ---------- getters ----------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHortaId() {
        return hortaId;
    }

    public String getPlantaId() {
        return plantaId;
    }

    public String getCanteiro() {
        return canteiro;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public LocalDate getDataPlantio() {
        return dataPlantio;
    }

    public StatusPlantio getStatus() {
        return status;
    }

    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }

    public List<RegistroRega> getRegas() {
        return Collections.unmodifiableList(regas);
    }

    public List<RegistroCuidado> getCuidados() {
        return Collections.unmodifiableList(cuidados);
    }
}
