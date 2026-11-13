package com.ecohorta.service;

import com.ecohorta.model.ItemSaude;
import com.ecohorta.model.Planta;
import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.TipoRegistroSaude;
import com.ecohorta.repository.PlantaRepository;
import com.ecohorta.repository.SaudeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Saúde e segurança: toxicidade para gatos, cães e pessoas e orientações por condição de saúde.
 * Informação educativa, que não substitui médico, nutricionista ou veterinário.
 */
public class SaudeService {

    /** Uma citação da planta em um registro de saúde. */
    public record Alerta(String codigo, String nome, TipoRegistroSaude tipo, ItemSaude item) {
    }

    /** Tudo o que o banco sabe sobre a planta em termos de saúde. */
    public record PerfilSaude(List<Alerta> toxicidade, List<Alerta> evitarEm,
                              List<Alerta> indicadaPara, List<Alerta> seguraParaPets) {

        public boolean semRegistros() {
            return toxicidade.isEmpty() && evitarEm.isEmpty() && indicadaPara.isEmpty() && seguraParaPets.isEmpty();
        }
    }

    private final SaudeRepository saude;
    private final PlantaRepository plantas;

    public SaudeService(SaudeRepository saude, PlantaRepository plantas) {
        this.saude = saude;
        this.plantas = plantas;
    }

    /**
     * Insere os registros de saúde que ainda não existem, ligando cada nome de planta ao seu id.
     * O catálogo precisa estar carregado antes.
     *
     * @return quantos registros foram inseridos
     */
    public int carregarSaudeInicial() {
        Map<String, Planta> cache = new HashMap<>();
        int inseridos = 0;
        for (SaudeInicial.Definicao d : SaudeInicial.registros()) {
            if (saude.buscarPorCodigo(d.codigo()).isPresent()) {
                continue;
            }
            RegistroSaude registro = new RegistroSaude(d.codigo(), d.tipo(), d.nome(), d.descricao(),
                    d.orientacao(), d.aviso());
            for (SaudeInicial.Entrada e : d.evitar()) {
                registro.adicionarEvitar(item(e, cache));
            }
            for (SaudeInicial.Entrada e : d.recomendadas()) {
                registro.adicionarRecomendada(item(e, cache));
            }
            saude.salvar(registro);
            inseridos++;
        }
        return inseridos;
    }

    private ItemSaude item(SaudeInicial.Entrada entrada, Map<String, Planta> cache) {
        Planta planta = cache.computeIfAbsent(entrada.planta(), nome -> plantas.buscarPorNome(nome)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Planta \"" + nome + "\" não encontrada no catálogo. Carregue o catálogo antes da saúde.")));
        return new ItemSaude(planta.getId(), planta.getNome(), entrada.motivo(), entrada.gravidade());
    }

    public List<RegistroSaude> listar() {
        return saude.listar();
    }

    public List<RegistroSaude> listarPorTipo(TipoRegistroSaude tipo) {
        return saude.listarPorTipo(tipo);
    }

    public RegistroSaude buscarPorCodigo(String codigo) {
        return saude.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de saúde não encontrado: " + codigo));
    }

    /** Monta a ficha de saúde da planta consultando as listas embutidas da coleção "saude". */
    public PerfilSaude perfil(Planta planta) {
        List<Alerta> toxicidade = new ArrayList<>();
        List<Alerta> evitarEm = new ArrayList<>();
        List<Alerta> indicadaPara = new ArrayList<>();
        List<Alerta> seguraParaPets = new ArrayList<>();

        for (RegistroSaude r : saude.listarPorPlanta(planta.getId())) {
            boolean condicao = r.getTipo() == TipoRegistroSaude.COMORBIDADE;
            for (ItemSaude i : r.getEvitar()) {
                if (i.plantaId().equals(planta.getId())) {
                    (condicao ? evitarEm : toxicidade).add(new Alerta(r.getCodigo(), r.getNome(), r.getTipo(), i));
                }
            }
            for (ItemSaude i : r.getRecomendadas()) {
                if (i.plantaId().equals(planta.getId())) {
                    (condicao ? indicadaPara : seguraParaPets).add(new Alerta(r.getCodigo(), r.getNome(), r.getTipo(), i));
                }
            }
        }
        return new PerfilSaude(toxicidade, evitarEm, indicadaPara, seguraParaPets);
    }
}
