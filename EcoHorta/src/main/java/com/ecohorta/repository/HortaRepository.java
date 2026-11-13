package com.ecohorta.repository;

import com.ecohorta.model.Horta;

import java.util.List;
import java.util.Optional;

/** Contrato de persistência de hortas (coleção "hortas"). */
public interface HortaRepository {

    Horta salvar(Horta horta);

    Optional<Horta> buscarPorId(String id);

    List<Horta> listarPorUsuario(String usuarioId);
}
