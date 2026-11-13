package com.ecohorta.repository;

import com.ecohorta.model.Estacao;
import com.ecohorta.model.Tempo;

import java.util.List;
import java.util.Optional;

public interface TempoRepository {

    Tempo salvar(Tempo tempo);

    Optional<Tempo> buscarPorEstacao(Estacao estacao);

    List<Tempo> listar();
}
