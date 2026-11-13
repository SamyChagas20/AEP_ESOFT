package com.ecohorta.repository;

import com.ecohorta.model.Planta;

import java.util.List;
import java.util.Optional;

/** Contrato de persistência do catálogo de plantas (coleção "plantas"). */
public interface PlantaRepository {

    Planta salvar(Planta planta);

    Optional<Planta> buscarPorId(String id);

    /** Busca pelo nome completo, ignorando maiúsculas e acentos. */
    Optional<Planta> buscarPorNome(String nome);

    /** Lista ordenada por nome. */
    List<Planta> listarTodas();

    /** Consulta com filtros combinados (E lógico), ordenada por nome. */
    List<Planta> filtrar(FiltroPlantas filtro);

    long contar();
}
