package com.ecohorta.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Optional;

public class Planta {

    private String id;
    private final String nome;
    private final String nomeCientifico;
    private final int diasParaColheita;
    private final String luminosidade;
    private final RegaRecomendada rega;
    private final List<JanelaPlantio> janelasPlantio = new ArrayList<>();
    private final List<Cuidado> cuidados = new ArrayList<>();
    private CategoriaPlanta categoria = CategoriaPlanta.OUTRA;
    private NivelLuz nivelLuz = NivelLuz.PLENA;
    private AdaptacaoApartamento apartamento = new AdaptacaoApartamento(NivelApartamento.NAO_RECOMENDADO, "");
    private boolean comestivel = true;

    public Planta(String nome, String nomeCientifico, int diasParaColheita,
                  String luminosidade, RegaRecomendada rega) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da planta é obrigatório.");
        }
        if (diasParaColheita < 1) {
            throw new IllegalArgumentException("Os dias para a colheita devem ser maiores que zero.");
        }
        if (rega == null) {
            throw new IllegalArgumentException("A recomendação de rega é obrigatória.");
        }
        this.nome = nome.trim();
        this.nomeCientifico = nomeCientifico == null ? "" : nomeCientifico.trim();
        this.diasParaColheita = diasParaColheita;
        this.luminosidade = luminosidade == null ? "" : luminosidade.trim();
        this.rega = rega;
    }

    public Planta adicionarJanela(int mesInicio, int mesFim, String observacao) {
        return adicionarJanela(new JanelaPlantio(mesInicio, mesFim, observacao));
    }

    public Planta adicionarJanela(JanelaPlantio janela) {
        janelasPlantio.add(Objects.requireNonNull(janela));
        return this;
    }

    public Planta adicionarCuidado(TipoCuidado tipo, String descricao, int frequenciaDias) {
        return adicionarCuidado(new Cuidado(tipo, descricao, frequenciaDias));
    }

    public Planta adicionarCuidado(Cuidado cuidado) {
        cuidados.add(Objects.requireNonNull(cuidado));
        return this;
    }

    public Planta categoria(CategoriaPlanta categoria) {
        this.categoria = Objects.requireNonNull(categoria);
        return this;
    }

    public Planta nivelLuz(NivelLuz nivelLuz) {
        this.nivelLuz = Objects.requireNonNull(nivelLuz);
        return this;
    }

    public Planta apartamento(AdaptacaoApartamento apartamento) {
        this.apartamento = Objects.requireNonNull(apartamento);
        return this;
    }

    public Planta comestivel(boolean comestivel) {
        this.comestivel = comestivel;
        return this;
    }

    public boolean epocaAdequada(int mes) {
        return janelaPara(mes).isPresent();
    }

    public boolean epocaAdequada(LocalDate data) {
        return epocaAdequada(data.getMonthValue());
    }

    public Optional<JanelaPlantio> janelaPara(int mes) {
        return janelasPlantio.stream().filter(j -> j.contem(mes)).findFirst();
    }

    public List<Estacao> getEstacoes() {
        List<Estacao> resultado = new ArrayList<>();
        for (Estacao estacao : Estacao.values()) {
            if (estacao.getMeses().stream().anyMatch(this::epocaAdequada)) {
                resultado.add(estacao);
            }
        }
        return resultado;
    }

    public OptionalInt getMelhorMesPlantio() {
        if (janelasPlantio.isEmpty()) {
            return OptionalInt.empty();
        }
        JanelaPlantio janela = janelasPlantio.get(0);
        int tamanho = ((janela.mesFim() - janela.mesInicio() + 12) % 12) + 1;
        if (tamanho == 12) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(((janela.mesInicio() - 1 + (tamanho - 1) / 2) % 12) + 1);
    }

    public List<Cuidado> cuidadosPeriodicos() {
        return cuidados.stream().filter(Cuidado::periodico).toList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeBusca() {
        return Textos.normalizar(nome);
    }

    public String getNomeCientifico() {
        return nomeCientifico;
    }

    public int getDiasParaColheita() {
        return diasParaColheita;
    }

    public String getLuminosidade() {
        return luminosidade;
    }

    public RegaRecomendada getRega() {
        return rega;
    }

    public CategoriaPlanta getCategoria() {
        return categoria;
    }

    public NivelLuz getNivelLuz() {
        return nivelLuz;
    }

    public int getHorasSolMinimas() {
        return nivelLuz.getHorasMinimas();
    }

    public AdaptacaoApartamento getApartamento() {
        return apartamento;
    }

    public boolean isComestivel() {
        return comestivel;
    }

    public List<JanelaPlantio> getJanelasPlantio() {
        return Collections.unmodifiableList(janelasPlantio);
    }

    public List<Cuidado> getCuidados() {
        return Collections.unmodifiableList(cuidados);
    }
}
