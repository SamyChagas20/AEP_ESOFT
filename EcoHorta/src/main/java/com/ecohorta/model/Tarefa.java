package com.ecohorta.model;

import java.time.LocalDate;

/** Tarefa sugerida para o dia, calculada a partir dos plantios ativos. */
public record Tarefa(TipoTarefa tipo,
                     String plantioId,
                     String plantaNome,
                     String canteiro,
                     String descricao,
                     LocalDate dataPrevista,
                     boolean atrasada) {
}
