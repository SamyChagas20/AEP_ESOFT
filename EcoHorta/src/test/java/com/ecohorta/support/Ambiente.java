package com.ecohorta.support;

import com.ecohorta.service.Servicos;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Ambiente {

    public static final LocalDate HOJE = LocalDate.of(2026, 11, 10);
    public static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-11-10T12:00:00Z"), ZoneOffset.UTC);

    public final InMemoryUsuarioRepository usuarios = new InMemoryUsuarioRepository();
    public final InMemoryHortaRepository hortas = new InMemoryHortaRepository();
    public final InMemoryPlantaRepository plantas = new InMemoryPlantaRepository();
    public final InMemoryPlantioRepository plantios = new InMemoryPlantioRepository();
    public final InMemoryTempoRepository tempos = new InMemoryTempoRepository();
    public final InMemorySaudeRepository saude = new InMemorySaudeRepository();
    public final Servicos servicos = Servicos.criar(usuarios, hortas, plantas, plantios, tempos, saude, RELOGIO);
}
