package com.ecohorta.cli;

import com.ecohorta.demo.DemoDataLoader;
import com.ecohorta.model.CategoriaPlanta;
import com.ecohorta.model.Canteiro;
import com.ecohorta.model.Cuidado;
import com.ecohorta.model.Estacao;
import com.ecohorta.model.Gravidade;
import com.ecohorta.model.Horta;
import com.ecohorta.model.ItemSaude;
import com.ecohorta.model.JanelaPlantio;
import com.ecohorta.model.MesDaEstacao;
import com.ecohorta.model.Meses;
import com.ecohorta.model.NivelApartamento;
import com.ecohorta.model.NivelLuz;
import com.ecohorta.model.Planta;
import com.ecohorta.model.Plantio;
import com.ecohorta.model.RegistroSaude;
import com.ecohorta.model.StatusPlantio;
import com.ecohorta.model.Tarefa;
import com.ecohorta.model.Tempo;
import com.ecohorta.model.TipoRegistroSaude;
import com.ecohorta.model.TipoTarefa;
import com.ecohorta.model.Usuario;
import com.ecohorta.repository.FiltroPlantas;
import com.ecohorta.service.RegraDeNegocioException;
import com.ecohorta.service.SaudeService;
import com.ecohorta.service.Servicos;

import java.io.PrintStream;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.function.Function;

public class ConsoleApp {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final Servicos servicos;
    private final DemoDataLoader demo;
    private final Scanner entrada;
    private final PrintStream saida;
    private final Clock relogio;

    public ConsoleApp(Servicos servicos, DemoDataLoader demo, Scanner entrada,
                      PrintStream saida, Clock relogio) {
        this.servicos = servicos;
        this.demo = demo;
        this.entrada = entrada;
        this.saida = saida;
        this.relogio = relogio;
    }

    public void executar() {
        saida.println("=======================================");
        saida.println("      EcoHorta - cuidando da horta     ");
        saida.println("=======================================");
        try {
            menuInicial();
        } catch (EntradaEncerradaException e) {
            saida.println("\nEntrada encerrada. Até a próxima!");
        }
    }

    private void menuInicial() {
        while (true) {
            saida.println("\n--- Menu inicial ---");
            saida.println("1 - Entrar / cadastrar-se (por e-mail)");
            saida.println("2 - Catálogo de plantas (busca, luz, apartamento, estação)");
            saida.println("3 - Estações do ano e melhores meses");
            saida.println("4 - O que plantar neste mês?");
            saida.println("5 - Saúde e segurança (pets e condições de saúde)");
            saida.println("6 - Carregar dados de demonstração e entrar");
            saida.println("0 - Sair");
            switch (ler("Escolha uma opção: ")) {
                case "1" -> executarAcao(this::entrar);
                case "2" -> executarAcao(this::menuCatalogo);
                case "3" -> executarAcao(this::menuEstacoes);
                case "4" -> executarAcao(this::oQuePlantar);
                case "5" -> executarAcao(this::menuSaude);
                case "6" -> executarAcao(this::entrarComDemonstracao);
                case "0" -> {
                    saida.println("Até a próxima! 🌱");
                    return;
                }
                default -> saida.println("Opção inválida.");
            }
        }
    }

    private void menuUsuario(Usuario usuario) {
        while (true) {
            saida.println("\n--- Olá, " + usuario.getNome() + "! ---");
            saida.println(" 1 - Minhas hortas");
            saida.println(" 2 - Criar horta");
            saida.println(" 3 - Adicionar canteiro a uma horta");
            saida.println(" 4 - Iniciar plantio");
            saida.println(" 5 - Ver plantios da horta");
            saida.println(" 6 - Registrar rega");
            saida.println(" 7 - Registrar cuidado");
            saida.println(" 8 - Tarefas de hoje");
            saida.println(" 9 - Encerrar plantio (colhido / perdido)");
            saida.println("10 - Resumo da horta");
            saida.println(" 0 - Sair da conta");
            switch (ler("Escolha uma opção: ")) {
                case "1" -> executarAcao(() -> minhasHortas(usuario));
                case "2" -> executarAcao(() -> criarHorta(usuario));
                case "3" -> executarAcao(() -> adicionarCanteiro(usuario));
                case "4" -> executarAcao(() -> iniciarPlantio(usuario));
                case "5" -> executarAcao(() -> verPlantios(usuario));
                case "6" -> executarAcao(() -> registrarRega(usuario));
                case "7" -> executarAcao(() -> registrarCuidado(usuario));
                case "8" -> executarAcao(() -> tarefasDeHoje(usuario));
                case "9" -> executarAcao(() -> encerrarPlantio(usuario));
                case "10" -> executarAcao(() -> resumo(usuario));
                case "0" -> {
                    return;
                }
                default -> saida.println("Opção inválida.");
            }
        }
    }

    private void executarAcao(Runnable acao) {
        try {
            acao.run();
        } catch (RegraDeNegocioException | IllegalArgumentException | IllegalStateException e) {
            saida.println("⚠ " + e.getMessage());
        }
    }

    private void entrar() {
        String email = lerObrigatorio("E-mail: ");
        Optional<Usuario> existente = servicos.usuarios().buscarPorEmail(email);
        Usuario usuario;
        if (existente.isPresent()) {
            usuario = existente.get();
            saida.println("Bem-vindo(a) de volta, " + usuario.getNome() + "!");
        } else {
            saida.println("E-mail novo! Vamos criar o seu cadastro.");
            String nome = lerObrigatorio("Seu nome: ");
            usuario = servicos.usuarios().cadastrar(nome, email);
            saida.println("Cadastro criado, " + usuario.getNome() + "!");
        }
        menuUsuario(usuario);
    }

    private void entrarComDemonstracao() {
        Usuario usuario = demo.carregar();
        saida.println("Dados de demonstração prontos. Entrando como "
                + usuario.getNome() + " (" + usuario.getEmail() + ").");
        menuUsuario(usuario);
    }

    private void menuCatalogo() {
        while (true) {
            saida.println("\n--- Catálogo de plantas ---");
            saida.println("1 - Buscar por nome");
            saida.println("2 - Filtrar por categoria");
            saida.println("3 - Filtrar por estação do ano");
            saida.println("4 - Plantas para apartamento (com filtro de luz)");
            saida.println("5 - Filtrar por quantidade de luz solar");
            saida.println("0 - Voltar");
            switch (ler("Escolha uma opção: ")) {
                case "1" -> {
                    String termo = lerObrigatorio("Nome (ou parte do nome): ");
                    explorar("Busca por \"" + termo + "\"",
                            servicos.catalogo().pesquisar(FiltroPlantas.todas().comTermo(termo)));
                }
                case "2" -> {
                    List<CategoriaPlanta> categorias = new ArrayList<>(List.of(CategoriaPlanta.values()));
                    categorias.remove(CategoriaPlanta.OUTRA);
                    CategoriaPlanta categoria = escolher("Categorias:", categorias, CategoriaPlanta::getRotulo);
                    explorar(categoria.getRotulo(),
                            servicos.catalogo().pesquisar(FiltroPlantas.todas().comCategoria(categoria)));
                }
                case "3" -> {
                    Estacao estacao = escolher("Estações:", List.of(Estacao.values()), Estacao::getNome);
                    explorar("Plantas de " + estacao.getNome(),
                            servicos.catalogo().pesquisar(FiltroPlantas.todas().comEstacao(estacao)));
                }
                case "4" -> {
                    NivelApartamento nivel = escolher("Adaptação ao apartamento:",
                            List.of(NivelApartamento.values()), NivelApartamento::getRotulo);
                    FiltroPlantas filtro = FiltroPlantas.todas().comApartamento(nivel);
                    saida.println("Luz disponível (Enter = qualquer):");
                    NivelLuz luz = escolherLuzOpcional();
                    if (luz != null) {
                        filtro = filtro.comNivelLuz(luz);
                    }
                    explorar(nivel.getRotulo() + (luz == null ? "" : " — " + luz.getRotulo()),
                            servicos.catalogo().pesquisar(filtro));
                }
                case "5" -> {
                    NivelLuz luz = escolher("Quanto sol a planta precisa:", List.of(NivelLuz.values()),
                            n -> n.getRotulo() + " — " + n.getDescricao());
                    explorar(luz.getRotulo(), servicos.catalogo().pesquisar(FiltroPlantas.todas().comNivelLuz(luz)));
                }
                case "0" -> {
                    return;
                }
                default -> saida.println("Opção inválida.");
            }
        }
    }

    private NivelLuz escolherLuzOpcional() {
        List<NivelLuz> niveis = List.of(NivelLuz.values());
        for (int i = 0; i < niveis.size(); i++) {
            saida.println("  " + (i + 1) + ") " + niveis.get(i).getRotulo() + " — " + niveis.get(i).getDescricao());
        }
        OptionalInt escolha = lerInteiroOpcional("Número (ou Enter): ", 1, niveis.size());
        return escolha.isPresent() ? niveis.get(escolha.getAsInt() - 1) : null;
    }

    private void explorar(String titulo, List<Planta> plantas) {
        if (plantas.isEmpty()) {
            saida.println("Nenhuma planta encontrada.");
            return;
        }
        final int tamanhoPagina = 20;
        int pagina = 0;
        while (true) {
            int inicio = pagina * tamanhoPagina;
            int fim = Math.min(inicio + tamanhoPagina, plantas.size());
            saida.println("\n" + titulo + " — " + plantas.size() + " planta(s), mostrando "
                    + (inicio + 1) + " a " + fim + ":");
            for (int i = inicio; i < fim; i++) {
                Planta p = plantas.get(i);
                saida.printf(PT_BR, "%4d) %-24s %s | %s | apto: %s%n", i + 1, p.getNome(),
                        p.getCategoria().getRotulo(), p.getNivelLuz().getRotulo(),
                        p.getApartamento().nivel().getRotulo());
            }
            String dica = "Número para ver detalhes" + (fim < plantas.size() ? ", P = próxima página" : "")
                    + (pagina > 0 ? ", A = página anterior" : "") + ", 0 = voltar: ";
            String entradaUsuario = ler(dica).toLowerCase(Locale.ROOT);
            if (entradaUsuario.equals("0")) {
                return;
            } else if (entradaUsuario.equals("p") && fim < plantas.size()) {
                pagina++;
            } else if (entradaUsuario.equals("a") && pagina > 0) {
                pagina--;
            } else {
                try {
                    int numero = Integer.parseInt(entradaUsuario);
                    if (numero >= 1 && numero <= plantas.size()) {
                        detalharPlanta(plantas.get(numero - 1));
                        continue;
                    }
                } catch (NumberFormatException ignorada) {

                }
                saida.println("Opção inválida.");
            }
        }
    }

    private <T> T escolher(String titulo, List<T> opcoes, Function<T, String> rotulo) {
        saida.println(titulo);
        for (int i = 0; i < opcoes.size(); i++) {
            saida.println("  " + (i + 1) + ") " + rotulo.apply(opcoes.get(i)));
        }
        return opcoes.get(lerInteiro("Escolha: ", 1, opcoes.size()) - 1);
    }

    private Optional<Planta> pesquisarEEscolher() {
        String termo = lerObrigatorio("Nome (ou parte do nome) da planta: ");
        List<Planta> achadas = servicos.catalogo().pesquisar(FiltroPlantas.todas().comTermo(termo));
        if (achadas.isEmpty()) {
            saida.println("Nenhuma planta encontrada para \"" + termo + "\".");
            return Optional.empty();
        }
        if (achadas.size() > 15) {
            saida.println(achadas.size() + " resultados; mostrando os 15 primeiros. Refine o nome para ver os demais.");
            achadas = achadas.subList(0, 15);
        }
        if (achadas.size() == 1) {
            saida.println("Planta: " + achadas.get(0).getNome());
            return Optional.of(achadas.get(0));
        }
        return Optional.of(escolher("Resultados:", achadas, Planta::getNome));
    }

    private void detalharPlanta(Planta planta) {
        saida.println("\n== " + planta.getNome() + " (" + planta.getNomeCientifico() + ") ==");
        saida.println("Categoria  : " + planta.getCategoria().getRotulo()
                + (planta.isComestivel() ? "" : " — NÃO use para consumo"));
        saida.println("Luz solar  : " + planta.getNivelLuz().getRotulo() + " (" + planta.getNivelLuz().getDescricao() + ")");
        saida.println("Apartamento: " + planta.getApartamento().nivel().getRotulo()
                + " — " + planta.getApartamento().observacao());
        saida.println("Plantio    : " + joinJanelas(planta));
        String estacoes = planta.getEstacoes().stream().map(Estacao::getNome).reduce((x, y) -> x + ", " + y).orElse("—");
        String melhor = planta.getMelhorMesPlantio().isPresent()
                ? Meses.nome(planta.getMelhorMesPlantio().getAsInt()) : "qualquer mês (ano todo)";
        saida.println("Estações   : " + estacoes + " | melhor mês para plantar: " + melhor);
        if (planta.isComestivel()) {
            saida.println("Colheita   : cerca de " + planta.getDiasParaColheita() + " dias após o plantio");
        }
        saida.println("Rega       : a cada " + planta.getRega().frequenciaDias() + " dia(s), cerca de "
                + planta.getRega().quantidadeMl() + " ml. " + planta.getRega().observacao());
        saida.println("Cuidados   :");
        for (Cuidado cuidado : planta.getCuidados()) {
            String frequencia = cuidado.periodico() ? " (a cada " + cuidado.frequenciaDias() + " dias)" : "";
            saida.println("   - " + cuidado.tipo().getRotulo() + ": " + cuidado.descricao() + frequencia);
        }
        imprimirSaude(planta);
    }

    private String joinJanelas(Planta planta) {
        return planta.getJanelasPlantio().stream().map(JanelaPlantio::descricao)
                .reduce((x, y) -> x + " | " + y).orElse("—");
    }

    private void imprimirSaude(Planta planta) {
        SaudeService.PerfilSaude perfil = servicos.saude().perfil(planta);
        saida.println("Saúde      :");
        if (perfil.semRegistros()) {
            saida.println("   Sem alertas no nosso cadastro (isso não garante que seja segura).");
            return;
        }
        for (SaudeService.Alerta a : perfil.toxicidade()) {
            saida.println("   ⚠ Perigosa para " + a.nome() + " [" + a.item().gravidade().getRotulo() + "]: "
                    + a.item().motivo());
        }
        for (SaudeService.Alerta a : perfil.evitarEm()) {
            saida.println("   ✋ Cuidado se tiver " + a.nome() + " [" + a.item().gravidade().getRotulo() + "]: "
                    + a.item().motivo());
        }
        for (SaudeService.Alerta a : perfil.indicadaPara()) {
            saida.println("   ✔ Indicada em " + a.nome() + ": " + a.item().motivo());
        }
        if (!perfil.seguraParaPets().isEmpty()) {
            String quem = perfil.seguraParaPets().stream().map(SaudeService.Alerta::nome)
                    .reduce((x, y) -> x + " e " + y).orElse("");
            saida.println("   🐾 Considerada segura para: " + quem);
        }
        saida.println("   (Informação educativa; não substitui médico, nutricionista ou veterinário.)");
    }

    private void menuEstacoes() {
        List<Tempo> tempos = servicos.tempo().listar();
        if (tempos.isEmpty()) {
            saida.println("Os dados das estações ainda não foram carregados.");
            return;
        }
        Estacao atual = servicos.tempo().estacaoDe(hoje());
        saida.println("\nHoje é " + hoje().format(FORMATO_DATA) + ": estamos na " + atual.getNome() + ".");
        while (true) {
            saida.println("\nEstações do ano:");
            for (int i = 0; i < tempos.size(); i++) {
                Tempo t = tempos.get(i);
                saida.println("  " + (i + 1) + ") " + t.getEstacao().getNome() + " — melhor mês: "
                        + Meses.nome(t.getMelhorMes()) + (t.getEstacao() == atual ? "  (estação atual)" : ""));
            }
            int escolha = lerInteiro("Número da estação (0 = voltar): ", 0, tempos.size());
            if (escolha == 0) {
                return;
            }
            mostrarEstacao(tempos.get(escolha - 1));
        }
    }

    private void mostrarEstacao(Tempo tempo) {
        Estacao estacao = tempo.getEstacao();
        List<Integer> meses = estacao.getMeses();
        saida.println("\n== " + estacao.getNome() + " (" + Meses.nome(meses.get(0)) + " a "
                + Meses.nome(meses.get(meses.size() - 1)) + ") ==");
        saida.println(tempo.getDescricao());
        saida.println("⭐ Melhor mês para plantar: " + Meses.nome(tempo.getMelhorMes())
                + " — " + tempo.getJustificativaMelhorMes());
        Map<Integer, Long> contagem = servicos.tempo().plantasPorMes(estacao);
        for (MesDaEstacao mes : tempo.getMeses()) {
            saida.println("  " + mes.nome() + (mes.melhor() ? " ⭐" : "") + ": " + mes.observacao()
                    + " [" + contagem.getOrDefault(mes.numero(), 0L) + " plantas podem ser plantadas]");
        }
        saida.println("1 - Ver as plantas desta estação");
        saida.println("2 - Ver só as plantas do melhor mês (" + Meses.nome(tempo.getMelhorMes()) + ")");
        saida.println("0 - Voltar");
        switch (lerInteiro("Opção: ", 0, 2)) {
            case 1 -> explorar("Plantas de " + estacao.getNome(), servicos.tempo().plantasDaEstacao(estacao));
            case 2 -> explorar("Plantas para " + Meses.nome(tempo.getMelhorMes()),
                    servicos.tempo().plantasDoMelhorMes(estacao));
            default -> {

            }
        }
    }

    private void oQuePlantar() {
        int mes = hoje().getMonthValue();
        Estacao estacao = Estacao.deMes(mes);
        saida.println("\nMês atual: " + Meses.nome(mes) + " (" + estacao.getNome() + ").");
        servicos.tempo().listar().stream().filter(t -> t.getEstacao() == estacao).findFirst()
                .ifPresent(t -> saida.println("Melhor mês da estação: " + Meses.nome(t.getMelhorMes())
                        + (t.getMelhorMes() == mes ? " (é agora!)" : "") + "."));
        explorar("Para plantar em " + Meses.nome(mes), servicos.catalogo().plantasParaPlantar(mes));
    }

    private void menuSaude() {
        while (true) {
            saida.println("\n--- Saúde e segurança ---");
            saida.println("1 - Plantas perigosas para gatos");
            saida.println("2 - Plantas perigosas para cães");
            saida.println("3 - Plantas que fazem mal às pessoas (consumo)");
            saida.println("4 - Saúde por condição (asma, diabetes, renal, gravidez...)");
            saida.println("5 - Ficha de saúde de uma planta");
            saida.println("0 - Voltar");
            switch (ler("Escolha uma opção: ")) {
                case "1" -> mostrarRegistro(servicos.saude().buscarPorCodigo("GATOS"));
                case "2" -> mostrarRegistro(servicos.saude().buscarPorCodigo("CAES"));
                case "3" -> mostrarRegistro(servicos.saude().buscarPorCodigo("PESSOAS"));
                case "4" -> {
                    List<RegistroSaude> condicoes = servicos.saude().listarPorTipo(TipoRegistroSaude.COMORBIDADE);
                    if (condicoes.isEmpty()) {
                        saida.println("Os dados de saúde ainda não foram carregados.");
                    } else {
                        mostrarRegistro(escolher("Condições:", condicoes, RegistroSaude::getNome));
                    }
                }
                case "5" -> pesquisarEEscolher().ifPresent(p -> {
                    saida.println("\n== " + p.getNome() + " ==");
                    imprimirSaude(p);
                });
                case "0" -> {
                    return;
                }
                default -> saida.println("Opção inválida.");
            }
        }
    }

    private void mostrarRegistro(RegistroSaude registro) {
        saida.println("\n== " + registro.getNome() + " ==");
        saida.println(registro.getDescricao());
        saida.println("Orientação: " + registro.getOrientacao());
        if (!registro.getEvitar().isEmpty()) {
            saida.println("\nPlantas a evitar ou com cuidado:");
            imprimirItens(registro.getEvitar(), true);
        }
        if (!registro.getRecomendadas().isEmpty()) {
            saida.println("\nPlantas "
                    + (registro.getTipo() == TipoRegistroSaude.ANIMAL ? "consideradas seguras" : "recomendadas") + ":");
            imprimirItens(registro.getRecomendadas(), false);
        }
        saida.println("\n" + registro.getAviso());
    }

    private void imprimirItens(List<ItemSaude> itens, boolean comGravidade) {
        List<ItemSaude> ordenados = new ArrayList<>(itens);
        if (comGravidade) {
            ordenados.sort(Comparator.comparing(ItemSaude::gravidade).reversed());
        }
        Map<String, List<String>> grupos = new LinkedHashMap<>();
        for (ItemSaude i : ordenados) {
            String chave = (comGravidade ? "[" + i.gravidade().getRotulo() + "] " : "") + i.motivo();
            grupos.computeIfAbsent(chave, k -> new ArrayList<>()).add(i.plantaNome());
        }
        grupos.forEach((chave, nomes) -> saida.println("  • " + chave + ":\n      " + String.join(", ", nomes)));
    }

    private void minhasHortas(Usuario usuario) {
        List<Horta> hortas = servicos.hortas().listarPorUsuario(usuario.getId());
        if (hortas.isEmpty()) {
            saida.println("Você ainda não tem hortas. Use a opção 2 para criar a primeira.");
            return;
        }
        for (Horta horta : hortas) {
            saida.println("\n" + horta.getNome() + " — " + horta.getLocalizacao());
            if (horta.getCanteiros().isEmpty()) {
                saida.println("   (sem canteiros)");
            }
            for (Canteiro c : horta.getCanteiros()) {
                saida.printf(PT_BR, "   • %s — %.1f m², %s%n", c.nome(), c.areaM2(), c.exposicaoSol());
            }
        }
    }

    private void criarHorta(Usuario usuario) {
        String nome = lerObrigatorio("Nome da horta: ");
        String localizacao = ler("Localização (opcional): ");
        Horta horta = servicos.hortas().criarHorta(usuario.getId(), nome, localizacao);
        saida.println("✅ Horta \"" + horta.getNome() + "\" criada. Agora adicione canteiros (opção 3).");
    }

    private void adicionarCanteiro(Usuario usuario) {
        Optional<Horta> horta = escolherHorta(usuario);
        if (horta.isEmpty()) {
            return;
        }
        String nome = lerObrigatorio("Nome do canteiro: ");
        double area = lerDecimalPositivo("Área em m²: ");
        String exposicao = ler("Exposição ao sol (ex.: Sol pleno, Meia sombra): ");
        servicos.hortas().adicionarCanteiro(horta.get().getId(), nome, area, exposicao);
        saida.println("✅ Canteiro \"" + nome + "\" adicionado.");
    }

    private void iniciarPlantio(Usuario usuario) {
        Optional<Horta> opcaoHorta = escolherHorta(usuario);
        if (opcaoHorta.isEmpty()) {
            return;
        }
        Horta horta = opcaoHorta.get();
        if (horta.getCanteiros().isEmpty()) {
            saida.println("Esta horta ainda não tem canteiros. Adicione um na opção 3.");
            return;
        }
        Optional<Planta> escolhida = pesquisarEEscolher();
        if (escolhida.isEmpty()) {
            return;
        }
        Planta planta = escolhida.get();
        saida.println("Luz: " + planta.getNivelLuz().getRotulo() + " | Apartamento: "
                + planta.getApartamento().nivel().getRotulo());
        servicos.saude().perfil(planta).toxicidade().forEach(a -> saida.println("⚠ Atenção: perigosa para "
                + a.nome() + " [" + a.item().gravidade().getRotulo() + "] — " + a.item().motivo()));

        List<Canteiro> canteiros = horta.getCanteiros();
        saida.println("Canteiros:");
        for (int i = 0; i < canteiros.size(); i++) {
            saida.println("  " + (i + 1) + ") " + canteiros.get(i).nome()
                    + " — " + canteiros.get(i).exposicaoSol());
        }
        Canteiro canteiro = canteiros.get(lerInteiro("Escolha o canteiro: ", 1, canteiros.size()) - 1);

        int quantidade = lerInteiro("Quantidade de mudas/plantas: ", 1, 10000);
        LocalDate data = lerData("Data do plantio (dd/MM/aaaa, Enter = hoje): ");

        if (!planta.epocaAdequada(data)) {
            saida.println("⚠ Atenção: este mês não está entre as épocas indicadas para " + planta.getNome() + ":");
            for (JanelaPlantio janela : planta.getJanelasPlantio()) {
                saida.println("   - " + janela.descricao());
            }
        }

        Plantio plantio = servicos.plantios()
                .iniciarPlantio(horta.getId(), planta.getNome(), canteiro.nome(), quantidade, data);
        String fim = planta.isComestivel()
                ? "Colheita prevista para " + plantio.dataPrevistaColheita(planta).format(FORMATO_DATA) + "."
                : "Planta ornamental: sem colheita.";
        saida.println("✅ Plantio iniciado: " + quantidade + " " + planta.getNome() + " em " + canteiro.nome()
                + ". " + fim);
    }

    private void verPlantios(Usuario usuario) {
        Optional<Horta> horta = escolherHorta(usuario);
        if (horta.isEmpty()) {
            return;
        }
        List<Plantio> plantios = servicos.plantios().listarPorHorta(horta.get().getId());
        if (plantios.isEmpty()) {
            saida.println("Nenhum plantio nesta horta. Use a opção 4 para iniciar um.");
            return;
        }
        for (Plantio plantio : plantios) {
            Planta planta = servicos.plantios().buscarPlanta(plantio.getPlantaId());
            saida.println("\n" + planta.getNome() + " — " + plantio.getCanteiro()
                    + " [" + plantio.getStatus().getRotulo() + "]");
            saida.println("   Plantado em " + plantio.getDataPlantio().format(FORMATO_DATA)
                    + " (" + plantio.getQuantidade() + " un.)"
                    + (planta.isComestivel()
                    ? " | colheita prevista em " + plantio.dataPrevistaColheita(planta).format(FORMATO_DATA)
                    : " | ornamental, sem colheita"));
            String ultima = plantio.ultimaRega().map(d -> d.format(FORMATO_DATA)).orElse("nunca");
            saida.println("   Regas: " + plantio.getRegas().size() + " (última: " + ultima + ")"
                    + " | Cuidados registrados: " + plantio.getCuidados().size());
        }
    }

    private void registrarRega(Usuario usuario) {
        Optional<Plantio> escolhido = escolherPlantioAtivo(usuario);
        if (escolhido.isEmpty()) {
            return;
        }
        Plantio plantio = escolhido.get();
        Planta planta = servicos.plantios().buscarPlanta(plantio.getPlantaId());

        OptionalInt ml = lerInteiroOpcional(
                "Quantidade em ml (Enter = " + planta.getRega().quantidadeMl() + "): ", 1, 100000);
        LocalDate data = lerData("Data da rega (Enter = hoje): ");
        String observacao = ler("Observação (opcional): ");

        Plantio atualizado = servicos.plantios().registrarRega(
                plantio.getId(), ml.isPresent() ? ml.getAsInt() : null, observacao, data);
        saida.println("💧 Rega registrada. Próxima rega sugerida: "
                + atualizado.proximaRega(planta).format(FORMATO_DATA) + ".");
    }

    private void registrarCuidado(Usuario usuario) {
        Optional<Plantio> escolhido = escolherPlantioAtivo(usuario);
        if (escolhido.isEmpty()) {
            return;
        }
        Plantio plantio = escolhido.get();
        Planta planta = servicos.plantios().buscarPlanta(plantio.getPlantaId());
        List<Cuidado> cuidados = planta.getCuidados();
        if (cuidados.isEmpty()) {
            saida.println("Esta planta não tem cuidados cadastrados.");
            return;
        }

        saida.println("Cuidados recomendados para " + planta.getNome() + ":");
        for (int i = 0; i < cuidados.size(); i++) {
            Cuidado c = cuidados.get(i);
            String frequencia = c.periodico() ? " (a cada " + c.frequenciaDias() + " dias)" : "";
            saida.println("  " + (i + 1) + ") [" + c.tipo().getRotulo() + "] " + c.descricao() + frequencia);
        }
        Cuidado cuidado = cuidados.get(lerInteiro("Qual cuidado você realizou? ", 1, cuidados.size()) - 1);
        LocalDate data = lerData("Data (Enter = hoje): ");

        Plantio atualizado = servicos.plantios()
                .registrarCuidado(plantio.getId(), cuidado.tipo(), cuidado.descricao(), data);
        String proximo = cuidado.periodico()
                ? " Próximo: " + atualizado.proximoCuidado(cuidado).format(FORMATO_DATA) + "."
                : "";
        saida.println("🌿 Cuidado registrado." + proximo);
    }

    private void tarefasDeHoje(Usuario usuario) {
        Optional<Horta> horta = escolherHorta(usuario);
        if (horta.isEmpty()) {
            return;
        }
        LocalDate hoje = hoje();
        List<Tarefa> tarefas = servicos.plantios().tarefasDoDia(horta.get().getId(), hoje);
        if (tarefas.isEmpty()) {
            saida.println("🎉 Nenhuma tarefa pendente para hoje!");
            return;
        }
        saida.println("Tarefas para " + hoje.format(FORMATO_DATA) + ":");
        for (Tarefa t : tarefas) {
            String atraso = t.atrasada() ? " [ATRASADA desde " + t.dataPrevista().format(FORMATO_DATA) + "]" : "";
            saida.println("  " + icone(t.tipo()) + " " + t.plantaNome() + " (" + t.canteiro() + ") — "
                    + t.descricao() + atraso);
        }
    }

    private void encerrarPlantio(Usuario usuario) {
        Optional<Plantio> escolhido = escolherPlantioAtivo(usuario);
        if (escolhido.isEmpty()) {
            return;
        }
        Plantio plantio = escolhido.get();
        saida.println("1) Colhido   2) Perdido   0) Cancelar");
        int opcao = lerInteiro("Opção: ", 0, 2);
        if (opcao == 0) {
            saida.println("Nada foi alterado.");
            return;
        }
        LocalDate data = lerData("Data (Enter = hoje): ");
        if (opcao == 1) {
            servicos.plantios().colher(plantio.getId(), data);
            saida.println("🧺 Plantio marcado como colhido.");
        } else {
            servicos.plantios().marcarComoPerdido(plantio.getId(), data);
            saida.println("Plantio marcado como perdido.");
        }
    }

    private void resumo(Usuario usuario) {
        Optional<Horta> opcaoHorta = escolherHorta(usuario);
        if (opcaoHorta.isEmpty()) {
            return;
        }
        Horta horta = opcaoHorta.get();
        Map<StatusPlantio, Long> contagem = servicos.plantios().resumo(horta.getId());
        saida.println("\nResumo de " + horta.getNome() + " (" + horta.getCanteiros().size() + " canteiro(s)):");
        for (StatusPlantio status : StatusPlantio.values()) {
            saida.println("  " + status.getRotulo() + ": " + contagem.getOrDefault(status, 0L));
        }
    }

    private Optional<Horta> escolherHorta(Usuario usuario) {
        List<Horta> hortas = servicos.hortas().listarPorUsuario(usuario.getId());
        if (hortas.isEmpty()) {
            saida.println("Você ainda não tem hortas. Crie uma na opção 2.");
            return Optional.empty();
        }
        if (hortas.size() == 1) {
            saida.println("Horta: " + hortas.get(0).getNome());
            return Optional.of(hortas.get(0));
        }
        saida.println("Suas hortas:");
        for (int i = 0; i < hortas.size(); i++) {
            saida.println("  " + (i + 1) + ") " + hortas.get(i).getNome());
        }
        return Optional.of(hortas.get(lerInteiro("Escolha a horta: ", 1, hortas.size()) - 1));
    }

    private Optional<Plantio> escolherPlantioAtivo(Usuario usuario) {
        Optional<Horta> horta = escolherHorta(usuario);
        if (horta.isEmpty()) {
            return Optional.empty();
        }
        List<Plantio> ativos = servicos.plantios().listarPorHorta(horta.get().getId()).stream()
                .filter(Plantio::isAtivo)
                .toList();
        if (ativos.isEmpty()) {
            saida.println("Não há plantios ativos nesta horta. Use a opção 4 para iniciar um.");
            return Optional.empty();
        }
        saida.println("Plantios ativos:");
        for (int i = 0; i < ativos.size(); i++) {
            Plantio p = ativos.get(i);
            Planta planta = servicos.plantios().buscarPlanta(p.getPlantaId());
            saida.println("  " + (i + 1) + ") " + planta.getNome() + " — " + p.getCanteiro()
                    + " (plantado em " + p.getDataPlantio().format(FORMATO_DATA) + ")");
        }
        return Optional.of(ativos.get(lerInteiro("Escolha o plantio: ", 1, ativos.size()) - 1));
    }

    private String icone(TipoTarefa tipo) {
        return switch (tipo) {
            case REGA -> "💧";
            case CUIDADO -> "🌿";
            case COLHEITA -> "🧺";
        };
    }

    private String ler(String pergunta) {
        saida.print(pergunta);
        saida.flush();
        if (!entrada.hasNextLine()) {
            throw new EntradaEncerradaException();
        }
        return entrada.nextLine().trim();
    }

    private String lerObrigatorio(String pergunta) {
        while (true) {
            String valor = ler(pergunta);
            if (!valor.isEmpty()) {
                return valor;
            }
            saida.println("Este campo é obrigatório.");
        }
    }

    private int lerInteiro(String pergunta, int minimo, int maximo) {
        while (true) {
            String valor = ler(pergunta);
            try {
                int numero = Integer.parseInt(valor);
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
            } catch (NumberFormatException ignorada) {

            }
            saida.println("Digite um número entre " + minimo + " e " + maximo + ".");
        }
    }

    private OptionalInt lerInteiroOpcional(String pergunta, int minimo, int maximo) {
        while (true) {
            String valor = ler(pergunta);
            if (valor.isEmpty()) {
                return OptionalInt.empty();
            }
            try {
                int numero = Integer.parseInt(valor);
                if (numero >= minimo && numero <= maximo) {
                    return OptionalInt.of(numero);
                }
            } catch (NumberFormatException ignorada) {

            }
            saida.println("Digite um número entre " + minimo + " e " + maximo + " (ou Enter).");
        }
    }

    private double lerDecimalPositivo(String pergunta) {
        while (true) {
            String valor = ler(pergunta).replace(',', '.');
            try {
                double numero = Double.parseDouble(valor);
                if (numero > 0) {
                    return numero;
                }
            } catch (NumberFormatException ignorada) {

            }
            saida.println("Digite um número maior que zero.");
        }
    }

    private LocalDate lerData(String pergunta) {
        while (true) {
            String valor = ler(pergunta);
            if (valor.isEmpty()) {
                return hoje();
            }
            try {
                return LocalDate.parse(valor, FORMATO_DATA);
            } catch (DateTimeParseException e) {
                saida.println("Data inválida. Use o formato dd/MM/aaaa.");
            }
        }
    }

    private LocalDate hoje() {
        return LocalDate.now(relogio);
    }
}
