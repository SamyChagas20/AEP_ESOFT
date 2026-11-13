package com.ecohorta.service;

import com.ecohorta.model.Planta;
import com.ecohorta.repository.FiltroPlantas;
import com.ecohorta.repository.PlantaRepository;

import java.util.List;

/** Consulta e manutenção do catálogo de plantas. */
public class CatalogoService {

    private final PlantaRepository plantas;

    public CatalogoService(PlantaRepository plantas) {
        this.plantas = plantas;
    }

    public List<Planta> listar() {
        return plantas.listarTodas();
    }

    /** Consulta com filtros (nome, estação, luz, apartamento, categoria), ordenada por nome. */
    public List<Planta> pesquisar(FiltroPlantas filtro) {
        return plantas.filtrar(filtro);
    }

    public Planta buscarPorNome(String nome) {
        return plantas.buscarPorNome(nome)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Planta não encontrada no catálogo: " + nome));
    }

    public Planta cadastrar(Planta planta) {
        if (plantas.buscarPorNome(planta.getNome()).isPresent()) {
            throw new RegraDeNegocioException("A planta \"" + planta.getNome() + "\" já está no catálogo.");
        }
        return plantas.salvar(planta);
    }

    /** Plantas cuja janela de plantio contém o mês informado (1-12). */
    public List<Planta> plantasParaPlantar(int mes) {
        return plantas.listarTodas().stream().filter(p -> p.epocaAdequada(mes)).toList();
    }

    /**
     * Garante que as plantas iniciais (5 detalhadas + CSV) existam no banco.
     * Não duplica: só insere as que ainda não estão cadastradas.
     *
     * @return quantidade de plantas inseridas
     */
    public int carregarCatalogoInicial() {
        int inseridas = 0;
        for (Planta planta : CatalogoInicial.plantas()) {
            if (plantas.buscarPorNome(planta.getNome()).isEmpty()) {
                plantas.salvar(planta);
                inseridas++;
            }
        }
        return inseridas;
    }
}
