package com.ecohorta.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma planta cadastrada na horta, com as informações
 * necessárias para orientar o cuidado dela.
 */
public class Planta {

    private String nome;
    private String epocaPlantio;
    private int frequenciaRegaDias; // a cada quantos dias deve ser regada
    private int diasParaColheita;
    private String cuidados;
    private LocalDate ultimaRega; // pode ser null se nunca foi regada

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Planta() {
    }

    public Planta(String nome, String epocaPlantio, int frequenciaRegaDias,
                  int diasParaColheita, String cuidados, LocalDate ultimaRega) {
        this.nome = nome;
        this.epocaPlantio = epocaPlantio;
        this.frequenciaRegaDias = frequenciaRegaDias;
        this.diasParaColheita = diasParaColheita;
        this.cuidados = cuidados;
        this.ultimaRega = ultimaRega;
    }

    // ---- Getters e Setters ----

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEpocaPlantio() {
        return epocaPlantio;
    }

    public void setEpocaPlantio(String epocaPlantio) {
        this.epocaPlantio = epocaPlantio;
    }

    public int getFrequenciaRegaDias() {
        return frequenciaRegaDias;
    }

    public void setFrequenciaRegaDias(int frequenciaRegaDias) {
        this.frequenciaRegaDias = frequenciaRegaDias;
    }

    public int getDiasParaColheita() {
        return diasParaColheita;
    }

    public void setDiasParaColheita(int diasParaColheita) {
        this.diasParaColheita = diasParaColheita;
    }

    public String getCuidados() {
        return cuidados;
    }

    public void setCuidados(String cuidados) {
        this.cuidados = cuidados;
    }

    public LocalDate getUltimaRega() {
        return ultimaRega;
    }

    public void setUltimaRega(LocalDate ultimaRega) {
        this.ultimaRega = ultimaRega;
    }

    /**
     * Calcula a próxima data recomendada para regar, com base na última rega.
     * Se nunca foi regada, retorna a data de hoje (precisa regar).
     */
    public LocalDate calcularProximaRega() {
        if (ultimaRega == null) {
            return LocalDate.now();
        }
        return ultimaRega.plusDays(frequenciaRegaDias);
    }

    /**
     * Indica se a planta precisa ser regada hoje (ou já passou da data).
     */
    public boolean precisaRegarHoje() {
        return !calcularProximaRega().isAfter(LocalDate.now());
    }

    @Override
    public String toString() {
        String rega = (ultimaRega == null) ? "nunca regada" : ultimaRega.format(FORMATO_DATA);
        return String.format(
                "%-12s | Época de plantio: %-30s | Rega a cada %d dia(s) | Última rega: %s",
                nome, epocaPlantio, frequenciaRegaDias, rega);
    }
}
