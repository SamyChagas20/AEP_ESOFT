package com.ecohorta.repository;

import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.Estacao;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;

/**
 * Critérios opcionais para consultar o catálogo (campos nulos são ignorados).
 * "termo" busca parte do nome, sem diferenciar acentos nem maiúsculas.
 */
public record FiltroPlantas(String termo, Estacao estacao, NivelLuz nivelLuz,
                            NivelApartamento apartamento, CategoriaPlanta categoria, Boolean comestivel) {

    public static FiltroPlantas todas() {
        return new FiltroPlantas(null, null, null, null, null, null);
    }

    public FiltroPlantas comTermo(String novo) {
        return new FiltroPlantas(novo, estacao, nivelLuz, apartamento, categoria, comestivel);
    }

    public FiltroPlantas comEstacao(Estacao nova) {
        return new FiltroPlantas(termo, nova, nivelLuz, apartamento, categoria, comestivel);
    }

    public FiltroPlantas comNivelLuz(NivelLuz novo) {
        return new FiltroPlantas(termo, estacao, novo, apartamento, categoria, comestivel);
    }

    public FiltroPlantas comApartamento(NivelApartamento novo) {
        return new FiltroPlantas(termo, estacao, nivelLuz, novo, categoria, comestivel);
    }

    public FiltroPlantas comCategoria(CategoriaPlanta nova) {
        return new FiltroPlantas(termo, estacao, nivelLuz, apartamento, nova, comestivel);
    }

    public FiltroPlantas comComestivel(Boolean novo) {
        return new FiltroPlantas(termo, estacao, nivelLuz, apartamento, categoria, novo);
    }
}
