package com.ecohorta.service;

import com.ecohorta.model.Planta;
import com.ecohorta.repository.FiltroPlantas;
import com.ecohorta.repository.PlantaRepository;

import java.util.List;

public class CatalogoService {

    private final PlantaRepository plantas;

    public CatalogoService(PlantaRepository plantas) {
        this.plantas = plantas;
    }

    public List<Planta> listar() {
        return plantas.listarTodas();
    }

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

    public List<Planta> plantasParaPlantar(int mes) {
        return plantas.listarTodas().stream().filter(p -> p.epocaAdequada(mes)).toList();
    }

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
