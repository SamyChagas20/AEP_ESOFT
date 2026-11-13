package com.ecohorta.repository;

import com.ecohorta.model.Plantio;
import com.ecohorta.model.StatusPlantio;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlantioRepository {

    Plantio salvar(Plantio plantio);

    Optional<Plantio> buscarPorId(String id);

    List<Plantio> listarPorHorta(String hortaId);

    Map<StatusPlantio, Long> contarPorStatus(String hortaId);
}
