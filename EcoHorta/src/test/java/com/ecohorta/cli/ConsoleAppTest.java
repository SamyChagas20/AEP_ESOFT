package com.ecohorta.cli;

import com.ecohorta.demo.DemoDataLoader;
import com.ecohorta.model.Usuario;
import com.ecohorta.support.Ambiente;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.ecohorta.support.Ambiente.RELOGIO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleAppTest {

    private final Ambiente ambiente = new Ambiente();

    private String executarBruto(String entrada) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Scanner scanner = new Scanner(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
        new ConsoleApp(ambiente.servicos, new DemoDataLoader(ambiente.servicos, RELOGIO),
                scanner, saida, RELOGIO).executar();
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private String executar(String... linhas) {
        return executarBruto(String.join("\n", linhas) + "\n");
    }

    private void carregarDados() {
        ambiente.servicos.carregarDadosIniciais();
    }

    @Test
    void sairImediatamente() {
        assertTrue(executar("0").contains("Até a próxima"));
    }

    @Test
    void opcaoInvalidaMostraAvisoEContinua() {
        String saida = executar("99", "0");
        assertTrue(saida.contains("Opção inválida."));
        assertTrue(saida.contains("Até a próxima"));
    }

    @Test
    void fimDaEntradaEncerraSemErro() {
        assertTrue(executarBruto("").contains("Entrada encerrada"));
    }

    @Test
    void buscaPorNomeSemAcentoMostraFichaCompletaDaPlanta() {
        carregarDados();
        String saida = executar("2", "1", "rucula", "1", "0", "0", "0");

        assertTrue(saida.contains("Busca por \"rucula\" — 2 planta(s)"));
        assertTrue(saida.contains("== Rúcula (Eruca vesicaria) =="));
        assertTrue(saida.contains("Luz solar  : Luz média (meio dia de sol)"));
        assertTrue(saida.contains("Apartamento: Ideal para apartamento"));
        assertTrue(saida.contains("Estações   : Primavera, Outono, Inverno | melhor mês para plantar: Junho"));
        assertTrue(saida.contains("Rega       : a cada 1 dia(s)"));
        assertTrue(saida.contains("✔ Indicada em"));
        assertTrue(saida.contains("✋ Cuidado se tiver"));
    }

    @Test
    void buscaSemResultadosAvisa() {
        carregarDados();
        assertTrue(executar("2", "1", "xyzxyz", "0", "0").contains("Nenhuma planta encontrada."));
    }

    @Test
    void numeroInvalidoNaListaPedeNovamente() {
        carregarDados();
        String saida = executar("2", "1", "rucula", "zzz", "99", "0", "0", "0");
        assertEquals(2, saida.split("Opção inválida\\.", -1).length - 1);
    }

    @Test
    void fichaDePlantaMostraAlertasDeToxicidadeParaPets() {
        carregarDados();
        String saida = executar("2", "1", "tomate-cereja", "1", "0", "0", "0");
        assertTrue(saida.contains("⚠ Perigosa para Gatos [Moderada]"));
        assertTrue(saida.contains("⚠ Perigosa para Cães [Moderada]"));
        assertTrue(saida.contains("Categoria  : Hortaliça de fruto"));
    }

    @Test
    void ornamentalNaoTemColheitaEMostraAvisoDeNaoConsumo() {
        carregarDados();
        String saida = executar("2", "1", "jiboia", "1", "0", "0", "0");
        assertTrue(saida.contains("NÃO use para consumo"));
        assertFalse(saida.contains("Colheita   :"));
        assertTrue(saida.contains("Luz solar  : Pouca luz"));
    }

    @Test
    void filtraPorCategoria() {
        carregarDados();
        String saida = executar("2", "2", "4", "0", "0", "0");
        assertTrue(saida.contains("Bulbo — 6 planta(s)"));
        assertTrue(saida.contains("Cebola-roxa"));
    }

    @Test
    void filtraPorEstacaoComPaginacao() {
        carregarDados();
        String saida = executar("2", "3", "4", "p", "a", "0", "0", "0");
        assertTrue(saida.contains("Plantas de Inverno"));
        assertTrue(saida.contains("mostrando 1 a 20"));
        assertTrue(saida.contains("mostrando 21 a 40"));
        assertTrue(saida.contains("A = página anterior"));
    }

    @Test
    void plantasParaApartamentoComPoucaLuz() {
        carregarDados();
        String saida = executar("2", "4", "1", "1", "0", "0", "0");
        assertTrue(saida.contains("Ideal para apartamento — Pouca luz"));
        assertTrue(saida.contains("Jiboia"));
        assertFalse(saida.contains("Tomate"));
    }

    @Test
    void plantasParaApartamentoSemFiltroDeLuz() {
        carregarDados();
        String saida = executar("2", "4", "2", "", "0", "0", "0");
        assertTrue(saida.contains("Possível com cuidados — "));
        assertFalse(saida.contains("Possível com cuidados — Pouca"));
    }

    @Test
    void filtraPorQuantidadeDeLuz() {
        carregarDados();
        String saida = executar("2", "5", "3", "0", "0", "0");
        assertTrue(saida.contains("Sol pleno (o dia todo)"));
        assertTrue(saida.contains("Sol pleno (o dia todo) |"));
    }

    @Test
    void submenuDoCatalogoRejeitaOpcaoInvalida() {
        carregarDados();
        assertTrue(executar("2", "9", "0", "0").contains("Opção inválida."));
    }

    @Test
    void estacoesMostramMelhorMesEQuantidadeDePlantasPorMes() {
        carregarDados();
        String saida = executar("3", "1", "2", "0", "0", "0");

        assertTrue(saida.contains("Hoje é 10/11/2026: estamos na Primavera."));
        assertTrue(saida.contains("Primavera — melhor mês: Outubro  (estação atual)"));
        assertTrue(saida.contains("⭐ Melhor mês para plantar: Outubro"));
        assertTrue(saida.contains("Outubro ⭐:"));
        assertTrue(saida.contains("plantas podem ser plantadas]"));
        assertTrue(saida.contains("Plantas para Outubro"));
    }

    @Test
    void estacaoPodeListarTodasAsPlantas() {
        carregarDados();
        String saida = executar("3", "4", "1", "0", "0", "0");
        assertTrue(saida.contains("Inverno — melhor mês: Agosto"));
        assertTrue(saida.contains("Plantas de Inverno"));
    }

    @Test
    void estacaoPermiteVoltarSemListar() {
        carregarDados();
        assertTrue(executar("3", "2", "0", "0", "0").contains("== Verão (Dezembro a Fevereiro) =="));
    }

    @Test
    void semDadosDeEstacoesAvisaParaCarregar() {
        assertTrue(executar("3", "0").contains("Os dados das estações ainda não foram carregados."));
    }

    @Test
    void oQuePlantarUsaOMesAtualEOMelhorMesDaEstacao() {
        carregarDados();
        String saida = executar("4", "0", "0");
        assertTrue(saida.contains("Mês atual: Novembro (Primavera)."));
        assertTrue(saida.contains("Melhor mês da estação: Outubro."));
        assertTrue(saida.contains("Para plantar em Novembro"));
    }

    @Test
    void oQuePlantarSemCatalogoAvisa() {
        assertTrue(executar("4", "0").contains("Nenhuma planta encontrada."));
    }

    @Test
    void mostraPlantasPerigosasParaGatos() {
        carregarDados();
        String saida = executar("5", "1", "0", "0");
        assertTrue(saida.contains("== Gatos =="));
        assertTrue(saida.contains("[Grave]"));
        assertTrue(saida.contains("Lírio"));
        assertTrue(saida.contains("consideradas seguras"));
        assertTrue(saida.contains("Informação educativa"));
    }

    @Test
    void mostraPlantasPerigosasParaCaesEPessoas() {
        carregarDados();
        String saida = executar("5", "2", "3", "0", "0");
        assertTrue(saida.contains("== Cães =="));
        assertTrue(saida.contains("Uvas causam insuficiência renal aguda"));
        assertTrue(saida.contains("== Pessoas (consumo) =="));
        assertTrue(saida.contains("Mandioca"));
    }

    @Test
    void mostraOQueEvitarERecomendarPorCondicaoDeSaude() {
        carregarDados();
        String saida = executar("5", "4", "10", "0", "0");
        assertTrue(saida.contains("== Uso de anticoagulante (varfarina) =="));
        assertTrue(saida.contains("Plantas a evitar ou com cuidado:"));
        assertTrue(saida.contains("Plantas recomendadas:"));
        assertTrue(saida.contains("Couve"));
        assertTrue(saida.contains("Cenoura"));
    }

    @Test
    void primeiraCondicaoEAlergiaAPolen() {
        carregarDados();
        assertTrue(executar("5", "4", "1", "0", "0").contains("== Alergia a pólen"));
    }

    @Test
    void fichaDeSaudeDeUmaPlantaEscolhidaPorBusca() {
        carregarDados();
        String saida = executar("5", "5", "cebola", "1", "0", "0");
        assertTrue(saida.contains("== Cebola =="));
        assertTrue(saida.contains("⚠ Perigosa para Gatos [Grave]"));
        assertTrue(saida.contains("⚠ Perigosa para Cães [Grave]"));
    }

    @Test
    void fichaDeSaudeDePlantaSemRegistrosAvisaQueNaoHaGarantia() {
        carregarDados();
        String saida = executar("5", "5", "tanchagem", "0", "0");
        assertTrue(saida.contains("Planta: Tanchagem"));
        assertTrue(saida.contains("Sem alertas no nosso cadastro"));
    }

    @Test
    void fichaDeSaudeSemResultadoAvisa() {
        carregarDados();
        assertTrue(executar("5", "5", "xyzxyz", "0", "0").contains("Nenhuma planta encontrada para \"xyzxyz\"."));
    }

    @Test
    void saudeSemDadosCarregadosAvisaSemQuebrar() {
        ambiente.servicos.catalogo().carregarCatalogoInicial();
        String saida = executar("5", "4", "0", "0");
        assertTrue(saida.contains("Os dados de saúde ainda não foram carregados."));
        assertTrue(executar("5", "1", "0").contains("⚠ Registro de saúde não encontrado: GATOS"));
    }

    @Test
    void submenuDeSaudeRejeitaOpcaoInvalida() {
        carregarDados();
        assertTrue(executar("5", "9", "0", "0").contains("Opção inválida."));
    }

    @Test
    void novoUsuarioEhCadastradoPeloEmail() {
        String saida = executar("1", "ana@exemplo.com", "Ana", "0", "0");
        assertTrue(saida.contains("E-mail novo!"));
        assertTrue(saida.contains("Cadastro criado, Ana!"));
        assertTrue(ambiente.servicos.usuarios().buscarPorEmail("ana@exemplo.com").isPresent());
    }

    @Test
    void usuarioExistenteEntraSemNovoCadastro() {
        ambiente.servicos.usuarios().cadastrar("Ana", "ana@exemplo.com");
        String saida = executar("1", "ANA@exemplo.com", "0", "0");
        assertTrue(saida.contains("Bem-vindo(a) de volta, Ana!"));
        assertEquals(1, ambiente.usuarios.listarTodos().size());
    }

    @Test
    void emailInvalidoMostraMensagemEVoltaAoMenu() {
        String saida = executar("1", "ana", "Ana", "0");
        assertTrue(saida.contains("⚠ E-mail inválido."));
        assertTrue(saida.contains("Até a próxima"));
    }

    @Test
    void camposObrigatoriosSaoPerguntadosNovamente() {
        String saida = executar("1", "", "ana@exemplo.com", "Ana", "0", "0");
        assertTrue(saida.contains("Este campo é obrigatório."));
    }

    @Test
    void semHortasTodasAsAcoesOrientamACriarUma() {
        ambiente.servicos.usuarios().cadastrar("Ana", "ana@exemplo.com");
        String saida = executar("1", "ana@exemplo.com",
                "1", "3", "4", "5", "6", "7", "8", "9", "10", "99", "0", "0");
        assertTrue(saida.contains("Você ainda não tem hortas. Use a opção 2"));
        assertTrue(saida.contains("Você ainda não tem hortas. Crie uma na opção 2."));
        assertTrue(saida.contains("Opção inválida."));
    }

    @Test
    void hortaSemCanteirosNaoPermiteIniciarPlantio() {
        carregarDados();
        String saida = executar("1", "ana@exemplo.com", "Ana",
                "2", "Quintal", "",
                "4",
                "1",
                "0", "0");
        assertTrue(saida.contains("Horta \"Quintal\" criada"));
        assertTrue(saida.contains("ainda não tem canteiros"));
        assertTrue(saida.contains("(sem canteiros)"));
    }

    @Test
    void hortaSemPlantiosMostraMensagensVazias() {
        carregarDados();
        String saida = executar("1", "ana@exemplo.com", "Ana",
                "2", "Quintal", "Maringá",
                "3", "Sol", "4", "Sol pleno",
                "5", "6", "8", "0", "0");
        assertTrue(saida.contains("Nenhum plantio nesta horta"));
        assertTrue(saida.contains("Não há plantios ativos nesta horta"));
        assertTrue(saida.contains("Nenhuma tarefa pendente para hoje!"));
    }

    @Test
    void fluxoCompletoDoCadastroAteAColheita() {
        carregarDados();
        String saida = executar(
                "1", "ana@exemplo.com", "Ana",
                "2", "Quintal", "Maringá",
                "3", "Canteiro A", "4,5", "Sol pleno",
                "4", "tomate", "1", "1", "3", "",
                "5",
                "6", "1", "", "", "",
                "7", "1", "1", "",
                "8",
                "9", "1", "1", "",
                "10",
                "1",
                "0", "0");

        assertTrue(saida.contains("Cadastro criado, Ana!"));
        assertTrue(saida.contains("Canteiro \"Canteiro A\" adicionado."));
        assertTrue(saida.contains("Resultados:"));
        assertTrue(saida.contains("Luz: Sol pleno (o dia todo) | Apartamento: Possível com cuidados"));
        assertTrue(saida.contains("⚠ Atenção: perigosa para Gatos [Moderada]"));
        assertTrue(saida.contains("Plantio iniciado: 3 Tomate em Canteiro A. Colheita prevista para 08/02/2027."));
        assertTrue(saida.contains("este mês não está entre as épocas indicadas para Tomate"));
        assertTrue(saida.contains("Tomate — Canteiro A [Ativo]"));
        assertTrue(saida.contains("Rega registrada. Próxima rega sugerida: 12/11/2026."));
        assertTrue(saida.contains("Cuidado registrado. Próximo: 25/11/2026."));
        assertTrue(saida.contains("marcado como colhido"));
        assertTrue(saida.contains("Colhido: 1"));
        assertTrue(saida.contains("Ativo: 0"));
        assertTrue(saida.contains("Canteiro A — 4,5 m², Sol pleno"));
    }

    @Test
    void plantioEmEpocaAdequadaNaoMostraAviso() {
        carregarDados();
        String saida = executar("1", "ana@exemplo.com", "Ana",
                "2", "Quintal", "",
                "3", "A", "2", "",
                "4", "pimenta", "2", "1", "2", "",
                "0", "0");
        assertTrue(saida.contains("Plantio iniciado: 2 Pimenta"));
        assertFalse(saida.contains("não está entre as épocas"));
    }

    @Test
    void plantioDeOrnamentalNaoTemColheita() {
        carregarDados();
        String saida = executar("1", "ana@exemplo.com", "Ana",
                "2", "Apê", "",
                "3", "Janela", "1", "Meia sombra",
                "4", "jiboia", "1", "1", "",
                "5", "8",
                "0", "0");
        assertTrue(saida.contains("Planta ornamental: sem colheita."));
        assertTrue(saida.contains("ornamental, sem colheita"));
        assertTrue(saida.contains("⚠ Atenção: perigosa para Gatos [Leve]"));
        assertTrue(saida.contains("Nenhuma tarefa pendente") || saida.contains("Regar"));
        assertFalse(saida.contains("Colheita prevista ("));
    }

    @Test
    void buscaDePlantaParaPlantioSemResultadoOuComMuitosResultados() {
        carregarDados();
        String saida = executar("1", "ana@exemplo.com", "Ana",
                "2", "Quintal", "",
                "3", "A", "2", "",
                "4", "xyzxyz",
                "4", "a", "1", "1", "1", "",
                "0", "0");
        assertTrue(saida.contains("Nenhuma planta encontrada para \"xyzxyz\"."));
        assertTrue(saida.contains("resultados; mostrando os 15 primeiros."));
        assertTrue(saida.contains("Plantio iniciado: 1 "));
    }

    @Test
    void validaEntradasInvalidasPedindoNovamente() {
        carregarDados();
        String saida = executar("1", "ana@exemplo.com", "Ana",
                "2", "Quintal", "",
                "3", "A", "abc", "-2", "2", "",
                "4", "alface-crespa", "1", "0", "2", "31/02/2026", "10/11/2026",
                "6", "1", "xyz", "300", "", "",
                "0", "0");
        assertTrue(saida.contains("Digite um número maior que zero."));
        assertTrue(saida.contains("Digite um número entre 1 e 10000."));
        assertTrue(saida.contains("Data inválida. Use o formato dd/MM/aaaa."));
        assertTrue(saida.contains("Digite um número entre 1 e 100000 (ou Enter)."));
        assertTrue(saida.contains("Rega registrada."));
    }

    @Test
    void dataDePlantioNoFuturoEhRecusada() {
        carregarDados();
        String saida = executar("1", "ana@exemplo.com", "Ana",
                "2", "Quintal", "",
                "3", "A", "2", "",
                "4", "alface-crespa", "1", "1", "11/11/2026",
                "0", "0");
        assertTrue(saida.contains("⚠ A data de plantio não pode estar no futuro."));
    }

    @Test
    void canteiroRepetidoMostraAvisoDeValidacao() {
        carregarDados();
        String saida = executar("1", "ana@exemplo.com", "Ana",
                "2", "Quintal", "",
                "3", "A", "2", "",
                "3", "a", "1", "",
                "0", "0");
        assertTrue(saida.contains("⚠ Já existe um canteiro chamado \"a\" nesta horta."));
    }

    @Test
    void comVariasHortasPedeParaEscolher() {
        ambiente.servicos.usuarios().cadastrar("Ana", "ana@exemplo.com");
        String saida = executar("1", "ana@exemplo.com",
                "2", "Horta A", "",
                "2", "Horta B", "",
                "3", "2", "Canteiro", "1", "",
                "1",
                "0", "0");
        assertTrue(saida.contains("Suas hortas:"));
        assertTrue(saida.contains("2) Horta B"));
        assertTrue(saida.contains("Canteiro \"Canteiro\" adicionado."));
    }

    @Test
    void demonstracaoCarregaDadosEMostraTarefasAtrasadas() {
        String saida = executar("6", "8", "0", "0");
        assertTrue(saida.contains("Entrando como Maria Hortelã (demo@ecohorta.com)"));
        assertTrue(saida.contains("Tarefas para 10/11/2026:"));
        assertTrue(saida.contains("[ATRASADA desde"));
        assertTrue(saida.contains("Colheita prevista"));
    }

    @Test
    void demonstracaoPermiteAcompanharEEncerrarPlantios() {
        String saida = executar("6",
                "5",
                "6", "3", "", "", "",
                "7", "3", "1", "",
                "9", "1", "2", "",
                "9", "1", "0",
                "10", "1",
                "0", "0");
        assertTrue(saida.contains("Tomate — Canteiro Sol [Ativo]"));
        assertTrue(saida.contains("Rega registrada. Próxima rega sugerida: 12/11/2026."));
        assertTrue(saida.contains("Cuidado registrado. Próximo: 25/11/2026."));
        assertTrue(saida.contains("Plantio marcado como perdido."));
        assertTrue(saida.contains("Nada foi alterado."));
        assertTrue(saida.contains("Perdido: 1"));
        assertTrue(saida.contains("Ativo: 4"));
        assertTrue(saida.contains("Canteiro Sombra — 2,5 m², Meia sombra"));
    }

    @Test
    void entrarComEmailDaDemonstracaoReaproveitaOsDados() {
        Usuario demo = new DemoDataLoader(ambiente.servicos, RELOGIO).carregar();
        String saida = executar("1", demo.getEmail(), "5", "0", "0");
        assertTrue(saida.contains("Bem-vindo(a) de volta, Maria Hortelã!"));
        assertTrue(saida.contains("Pimenta — Canteiro Sol [Ativo]"));
    }
}
