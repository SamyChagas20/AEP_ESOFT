package com.ecohorta.repository;

import com.ecohorta.model.Planta;

import java.util.List;
import java.util.Optional;

public interface PlantaRepository {

    Planta salvar(Planta planta);

    Optional<Planta> buscarPorId(String id);

    Optional<Planta> buscarPorNome(String nome);

    List<Planta> listarTodas();

    List<Planta> filtrar(FiltroPlantas filtro);

    long contar();
}
