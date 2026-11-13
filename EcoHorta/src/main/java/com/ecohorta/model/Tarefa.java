package com.ecohorta.model;

import java.time.LocalDate;

public record Tarefa(TipoTarefa tipo,
                     String plantioId,
                     String plantaNome,
                     String canteiro,
                     String descricao,
                     LocalDate dataPrevista,
                     boolean atrasada) {
}
