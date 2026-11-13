package com.ecohorta.demo;

import com.ecohorta.model.Horta;
import com.ecohorta.model.Usuario;
import com.ecohorta.service.Servicos;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Cria um usuário, uma horta com dois canteiros e plantios com histórico
 * para demonstrar rapidamente o sistema (tarefas atrasadas, colheita, etc.).
 * É idempotente: se o usuário de demonstração já existir, apenas o devolve.
 */
public class DemoDataLoader {

    public static final String EMAIL_DEMO = "demo@ecohorta.com";

    private final Servicos servicos;
    private final Clock relogio;

    public DemoDataLoader(Servicos servicos, Clock relogio) {
        this.servicos = servicos;
        this.relogio = relogio;
    }

    public Usuario carregar() {
        servicos.catalogo().carregarCatalogoInicial();

        Optional<Usuario> existente = servicos.usuarios().buscarPorEmail(EMAIL_DEMO);
        if (existente.isPresent()) {
            return existente.get();
        }

        LocalDate hoje = LocalDate.now(relogio);
        Usuario usuario = servicos.usuarios().cadastrar("Maria Hortelã", EMAIL_DEMO);

        Horta horta = servicos.hortas().criarHorta(usuario.getId(), "Horta do Quintal", "Maringá - PR");
        horta = servicos.hortas().adicionarCanteiro(horta.getId(), "Canteiro Sol", 4.0, "Sol pleno");
        horta = servicos.hortas().adicionarCanteiro(horta.getId(), "Canteiro Sombra", 2.5, "Meia sombra");
        String hortaId = horta.getId();

        var plantios = servicos.plantios();

        var tomate = plantios.iniciarPlantio(hortaId, "Tomate", "Canteiro Sol", 4, hoje.minusDays(20));
        plantios.registrarRega(tomate.getId(), 1000, "Rega da manhã", hoje.minusDays(3));

        var pimenta = plantios.iniciarPlantio(hortaId, "Pimenta", "Canteiro Sol", 2, hoje.minusDays(5));
        plantios.registrarRega(pimenta.getId(), null, "", hoje.minusDays(1));

        var cebolinha = plantios.iniciarPlantio(hortaId, "Cebolinha", "Canteiro Sombra", 10, hoje.minusDays(65));
        plantios.registrarRega(cebolinha.getId(), null, "", hoje.minusDays(1));

        var alface = plantios.iniciarPlantio(hortaId, "Alface", "Canteiro Sombra", 12, hoje.minusDays(10));
        plantios.registrarRega(alface.getId(), 250, "", hoje);

        var salsinha = plantios.iniciarPlantio(hortaId, "Salsinha", "Canteiro Sombra", 6, hoje.minusDays(30));
        plantios.registrarRega(salsinha.getId(), null, "", hoje.minusDays(2));

        return usuario;
    }
}
