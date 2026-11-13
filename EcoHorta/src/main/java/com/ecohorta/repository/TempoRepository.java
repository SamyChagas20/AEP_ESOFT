package com.ecohorta.repository;

import com.ecohorta.model.Estacao;
import com.ecohorta.model.Tempo;

import java.util.List;
import java.util.Optional;

/** Contrato de persistência das estações do ano (coleção "tempo"). */
public interface TempoRepository {

    Tempo salvar(Tempo tempo);

    Optional<Tempo> buscarPorEstacao(Estacao estacao);

    /** Estações em ordem (primavera, verão, outono, inverno). */
    List<Tempo> listar();
}
