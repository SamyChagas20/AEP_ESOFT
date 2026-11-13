package com.ecohorta.service;

import com.ecohorta.model.Canteiro;
import com.ecohorta.model.Horta;
import com.ecohorta.repository.HortaRepository;
import com.ecohorta.repository.UsuarioRepository;

import java.util.List;

public class HortaService {

    private final HortaRepository hortas;
    private final UsuarioRepository usuarios;

    public HortaService(HortaRepository hortas, UsuarioRepository usuarios) {
        this.hortas = hortas;
        this.usuarios = usuarios;
    }

    public Horta criarHorta(String usuarioId, String nome, String localizacao) {
        usuarios.buscarPorId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        return hortas.salvar(new Horta(usuarioId, nome, localizacao));
    }

    public Horta adicionarCanteiro(String hortaId, String nome, double areaM2, String exposicaoSol) {
        Horta horta = buscarPorId(hortaId);
        horta.adicionarCanteiro(new Canteiro(nome, areaM2, exposicaoSol));
        return hortas.salvar(horta);
    }

    public Horta buscarPorId(String hortaId) {
        return hortas.buscarPorId(hortaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Horta não encontrada."));
    }

    public List<Horta> listarPorUsuario(String usuarioId) {
        return hortas.listarPorUsuario(usuarioId);
    }
}
