package com.ecohorta;

import com.ecohorta.db.MongoConnection;
import com.ecohorta.model.Planta;
import com.ecohorta.service.HortaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * EcoHorta - Protótipo de terminal
 *
 * Ajuda a pessoa a cuidar da horta indicando época de plantio,
 * frequência de rega e cuidados gerais de 5 plantas cadastradas
 * inicialmente: Tomate, Cebolinha, Pimenta, Alface e Salsinha.
 *
 * Persistência: MongoDB (banco "ecohorta", coleção "plantas").
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HortaService service = new HortaService();

    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("        🌱 EcoHorta - Protótipo 🌱     ");
        System.out.println("=======================================");

        try {
            System.out.println("Conectando ao MongoDB...");
            service.garantirPlantasIniciais();
            System.out.println("Conectado! Banco pronto para uso.\n");

            menuPrincipal();

        } catch (Exception e) {
            System.out.println("\n❌ Não foi possível conectar ao MongoDB.");
            System.out.println("Verifique se o MongoDB está rodando em mongodb://localhost:27017");
            System.out.println("Detalhe do erro: " + e.getMessage());
        } finally {
            MongoConnection.fechar();
            scanner.close();
        }
    }

    private static void menuPrincipal() {
        boolean sair = false;
        while (!sair) {
            System.out.println("---------------------------------------");
            System.out.println("1 - Listar todas as plantas");
            System.out.println("2 - Ver detalhes de uma planta");
            System.out.println("3 - Registrar rega de hoje");
            System.out.println("4 - Ver plantas que precisam de rega hoje");
            System.out.println("5 - Cadastrar nova planta");
            System.out.println("0 - Sair");
            System.out.println("---------------------------------------");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine().trim();
            System.out.println();

            switch (opcao) {
                case "1" -> listarPlantas();
                case "2" -> verDetalhes();
                case "3" -> registrarRega();
                case "4" -> plantasParaRegarHoje();
                case "5" -> cadastrarPlanta();
                case "0" -> {
                    sair = true;
                    System.out.println("Até a próxima! 🌿");
                }
                default -> System.out.println("Opção inválida, tente novamente.\n");
            }
        }
    }

    private static void listarPlantas() {
        List<Planta> plantas = service.listarTodas();
        System.out.println("== Plantas cadastradas (" + plantas.size() + ") ==");
        for (Planta p : plantas) {
            System.out.println("- " + p);
        }
        System.out.println();
    }

    private static void verDetalhes() {
        System.out.print("Nome da planta: ");
        String nome = scanner.nextLine().trim();
        Planta p = service.buscarPorNome(nome);

        if (p == null) {
            System.out.println("Planta não encontrada.\n");
            return;
        }

        System.out.println("== " + p.getNome() + " ==");
        System.out.println("Época de plantio : " + p.getEpocaPlantio());
        System.out.println("Rega recomendada : a cada " + p.getFrequenciaRegaDias() + " dia(s)");
        System.out.println("Dias até colheita: " + p.getDiasParaColheita());
        System.out.println("Cuidados         : " + p.getCuidados());
        System.out.println("Última rega      : " +
                (p.getUltimaRega() == null ? "ainda não regada" : p.getUltimaRega().format(Planta.FORMATO_DATA)));
        System.out.println("Precisa regar hoje? " + (p.precisaRegarHoje() ? "SIM ⚠️" : "não"));
        System.out.println();
    }

    private static void registrarRega() {
        System.out.print("Nome da planta que foi regada agora: ");
        String nome = scanner.nextLine().trim();

        boolean ok = service.registrarRegaHoje(nome);
        if (ok) {
            System.out.println("✅ Rega registrada para " + nome + " em " +
                    LocalDate.now().format(Planta.FORMATO_DATA) + ".\n");
        } else {
            System.out.println("Planta não encontrada ou nada foi alterado.\n");
        }
    }

    private static void plantasParaRegarHoje() {
        List<Planta> plantas = service.plantasParaRegarHoje();
        if (plantas.isEmpty()) {
            System.out.println("🎉 Nenhuma planta precisa de rega hoje!\n");
            return;
        }
        System.out.println("⚠️  Plantas que precisam de rega hoje:");
        for (Planta p : plantas) {
            System.out.println("- " + p.getNome());
        }
        System.out.println();
    }

    private static void cadastrarPlanta() {
        System.out.println("== Cadastro de nova planta ==");
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Época de plantio: ");
        String epoca = scanner.nextLine().trim();

        System.out.print("Frequência de rega (a cada quantos dias): ");
        int freqRega = lerInteiro();

        System.out.print("Dias até a colheita: ");
        int diasColheita = lerInteiro();

        System.out.print("Cuidados: ");
        String cuidados = scanner.nextLine().trim();

        Planta nova = new Planta(nome, epoca, freqRega, diasColheita, cuidados, null);
        service.adicionarPlanta(nova);

        System.out.println("✅ Planta \"" + nome + "\" cadastrada com sucesso!\n");
    }

    private static int lerInteiro() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Digite um número válido: ");
            }
        }
    }
}
