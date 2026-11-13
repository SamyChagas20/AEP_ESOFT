package com.ecohorta.repository;

import com.ecohorta.model.Plantio;
import com.ecohorta.model.StatusPlantio;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Contrato de persistência de plantios (coleção "plantios"). */
public interface PlantioRepository {

    Plantio salvar(Plantio plantio);

    Optional<Plantio> buscarPorId(String id);

    /** Lista os plantios de uma horta, do mais antigo para o mais recente. */
    List<Plantio> listarPorHorta(String hortaId);

    /** Quantidade de plantios da horta por status (todos os status presentes, com 0 se não houver). */
    Map<StatusPlantio, Long> contarPorStatus(String hortaId);
}
