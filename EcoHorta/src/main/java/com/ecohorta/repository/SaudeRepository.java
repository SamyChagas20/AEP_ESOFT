package com.ecohorta.repository;

import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.TipoRegistroSaude;

import java.util.List;
import java.util.Optional;

/** Contrato de persistência de saúde e segurança (coleção "saude"). */
public interface SaudeRepository {

    RegistroSaude salvar(RegistroSaude registro);

    Optional<RegistroSaude> buscarPorCodigo(String codigo);

    /** Todos os registros, ordenados por nome. */
    List<RegistroSaude> listar();

    List<RegistroSaude> listarPorTipo(TipoRegistroSaude tipo);

    /** Registros que citam a planta (em "evitar" ou em "recomendadas"). */
    List<RegistroSaude> listarPorPlanta(String plantaId);
}
