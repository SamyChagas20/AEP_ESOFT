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

        List<Planta> iniciais = new ArrayList<>();

        iniciais.add(new Planta(
                "Tomate",
                "Início da primavera (setembro a outubro)",
                2,
                90,
                "Sol pleno (mín. 6h/dia); usar estacas ou tutores; adubar a cada 15 dias; remover brotos laterais.",
                null
        ));

        iniciais.add(new Planta(
                "Cebolinha",
                "Ano todo, preferindo clima ameno",
                1,
                60,
                "Aceita meia sombra; manter o solo sempre úmido; colher cortando rente ao solo para rebrotar.",
                null
        ));

        iniciais.add(new Planta(
                "Pimenta",
                "Primavera (setembro a novembro)",
                2,
                100,
                "Sol pleno; evitar encharcamento do solo; adubação rica em potássio na fase de frutificação.",
                null
        ));

        iniciais.add(new Planta(
                "Alface",
                "Outono/Inverno (março a junho) e final do inverno",
                1,
                45,
                "Prefere meia sombra em dias quentes; solo rico em matéria orgânica; regas leves e frequentes.",
                null
        ));

        iniciais.add(new Planta(
                "Salsinha",
                "Ano todo, evitando calor excessivo",
                2,
                70,
                "Meia sombra; manter o solo úmido mas não encharcado; colher as folhas externas primeiro.",
                null
        ));

        for (Planta p : iniciais) {
            dao.inserir(p);
        }
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
