package com.ecohorta.service;

import com.ecohorta.repository.HortaRepository;
import com.ecohorta.repository.PlantaRepository;
import com.ecohorta.repository.PlantioRepository;
import com.ecohorta.repository.SaudeRepository;
import com.ecohorta.repository.TempoRepository;
import com.ecohorta.repository.UsuarioRepository;

import java.time.Clock;

/** Agrupa os serviços da aplicação para facilitar a injeção (ex.: no terminal e nos testes). */
public record Servicos(UsuarioService usuarios,
                       HortaService hortas,
                       CatalogoService catalogo,
                       PlantioService plantios,
                       TempoService tempo,
                       SaudeService saude) {

    /** Quantidade de registros inseridos na carga inicial. */
    public record ResumoCarga(int plantas, int estacoes, int registrosSaude) {
    }

    public static Servicos criar(UsuarioRepository usuarios, HortaRepository hortas,
                                 PlantaRepository plantas, PlantioRepository plantios,
                                 TempoRepository tempos, SaudeRepository saude, Clock relogio) {
        return new Servicos(
                new UsuarioService(usuarios),
                new HortaService(hortas, usuarios),
                new CatalogoService(plantas),
                new PlantioService(plantios, hortas, plantas, relogio),
                new TempoService(tempos, plantas),
                new SaudeService(saude, plantas));
    }

    /** Carrega, nesta ordem, catálogo, estações e saúde (que depende do catálogo). Não duplica dados. */
    public ResumoCarga carregarDadosIniciais() {
        int plantas = catalogo.carregarCatalogoInicial();
        int estacoes = tempo.carregarTempoInicial();
        int saudeInserida = saude.carregarSaudeInicial();
        return new ResumoCarga(plantas, estacoes, saudeInserida);
    }
}
