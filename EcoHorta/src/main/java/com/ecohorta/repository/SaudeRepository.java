package com.ecohorta.repository;

import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.TipoRegistroSaude;

import java.util.List;
import java.util.Optional;

public interface SaudeRepository {

    RegistroSaude salvar(RegistroSaude registro);

    Optional<RegistroSaude> buscarPorCodigo(String codigo);

    List<RegistroSaude> listar();

    List<RegistroSaude> listarPorTipo(TipoRegistroSaude tipo);

    List<RegistroSaude> listarPorPlanta(String plantaId);
}
