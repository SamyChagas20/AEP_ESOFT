package com.ecohorta;

import com.ecohorta.cli.ConsoleApp;
import com.ecohorta.config.MongoConnection;
import com.ecohorta.demo.DemoDataLoader;
import com.ecohorta.repository.mongo.MongoHortaRepository;
import com.ecohorta.repository.mongo.MongoIndices;
import com.ecohorta.repository.mongo.MongoPlantaRepository;
import com.ecohorta.repository.mongo.MongoPlantioRepository;
import com.ecohorta.repository.mongo.MongoSaudeRepository;
import com.ecohorta.repository.mongo.MongoTempoRepository;
import com.ecohorta.repository.mongo.MongoUsuarioRepository;
import com.ecohorta.service.Servicos;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        PrintStream saida = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        try (MongoConnection conexao = MongoConnection.deAmbiente()) {
            saida.println("Conectando ao MongoDB...");
            conexao.verificarConexao();

            MongoDatabase banco = conexao.getBanco();
            MongoIndices.criar(banco);

            Clock relogio = Clock.systemDefaultZone();
            Servicos servicos = Servicos.criar(
                    new MongoUsuarioRepository(banco),
                    new MongoHortaRepository(banco),
                    new MongoPlantaRepository(banco),
                    new MongoPlantioRepository(banco),
                    new MongoTempoRepository(banco),
                    new MongoSaudeRepository(banco),
                    relogio);

            Servicos.ResumoCarga carga = servicos.carregarDadosIniciais();
            if (carga.plantas() + carga.estacoes() + carga.registrosSaude() > 0) {
                saida.println("Dados iniciais carregados: " + carga.plantas() + " planta(s), "
                        + carga.estacoes() + " estação(ões) e " + carga.registrosSaude()
                        + " registro(s) de saúde.");
            }

            Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8);
            new ConsoleApp(servicos, new DemoDataLoader(servicos, relogio), entrada, saida, relogio).executar();

        } catch (MongoException e) {
            saida.println("\n❌ Não foi possível usar o MongoDB (" + MongoConnection.URI_PADRAO + " por padrão).");
            saida.println("Verifique se ele está em execução (veja o README: 'docker compose up -d').");
            saida.println("Detalhe: " + e.getMessage());
            System.exit(1);
        }
    }
}
