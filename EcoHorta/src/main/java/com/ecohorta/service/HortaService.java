package com.ecohorta.service;

import com.ecohorta.dao.PlantaDAO;
import com.ecohorta.model.Planta;

import java.util.ArrayList;
import java.util.List;

/**
 * Regras de negócio do EcoHorta: cadastro inicial (seed) e consultas
 * de conveniência usadas pelo menu do terminal.
 */
public class HortaService {

    private final PlantaDAO dao = new PlantaDAO();

    /**
     * Garante que as 5 plantas padrão do protótipo existam no banco.
     * Só insere se a coleção estiver vazia (não duplica em execuções futuras).
     */
    public void garantirPlantasIniciais() {
        if (!dao.colecaoVazia()) {
            return;
        }
        dao.inserir(new Planta("Tomate", "Outono/Primavera", 2, 90, "Sol pleno, rega regular", null));
        dao.inserir(new Planta("Cebolinha", "Ano todo", 1, 60, "Sombra parcial ou sol", null));
        dao.inserir(new Planta("Pimenta", "Primavera/Verão", 3, 120, "Necessita de bastante calor e sol", null));
        dao.inserir(new Planta("Alface", "Outono/Inverno", 2, 45, "Solo úmido e boa drenagem", null));
        dao.inserir(new Planta("Salsinha", "Ano todo", 2, 70, "Manter solo levemente úmido", null));
    }

    public List<Planta> listarTodas() {
        return dao.listarTodas();
    }

    public Planta buscarPorNome(String nome) {
        return dao.buscarPorNome(nome);
    }

    public boolean registrarRegaHoje(String nome) {
        return dao.registrarRega(nome, java.time.LocalDate.now());
    }

    public void adicionarPlanta(Planta planta) {
        dao.inserir(planta);
    }

    /** Retorna as plantas que precisam ser regadas hoje (ou já passaram da data). */
    public List<Planta> plantasParaRegarHoje() {
        List<Planta> resultado = new ArrayList<>();
        for (Planta p : dao.listarTodas()) {
            if (p.precisaRegarHoje()) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
