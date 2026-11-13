# 🌱 EcoHorta

Assistente de horta caseira em **Java + MongoDB**: indica a **época de plantio**,
a **rega** e os **cuidados** de cada planta, registra o histórico de cada plantio
e calcula as **tarefas do dia** (regar, adubar, colher...).

Protótipo sem front-end: roda no terminal (ex.: terminal integrado do VSCode).
O catálogo traz **mais de 240 plantas** (hortaliças, ervas, frutíferas, medicinais, PANC, flores e ornamentais de
apartamento), com **época de plantio por estação do ano**, **quanto sol cada uma precisa**, **adaptação a apartamento**
e **alertas de saúde**: toxicidade para gatos, cães e pessoas, e o que escolher ou evitar em condições como asma,
diabetes, doença renal, uso de anticoagulante e gravidez.

## Requisitos da entrega × onde estão

| Critério | Como foi atendido |
|---|---|
| Banco NoSQL | MongoDB (driver oficial `mongodb-driver-sync`), com índices, agregação (`$group`) e `$lookup` |
| Programação OO | Entidades (`Planta`, `Plantio`, `Horta`, `Usuario`), *value objects* (records), serviços, repositórios (interfaces + implementação Mongo), CLI separada da regra de negócio |
| Múltiplas coleções + relacionamentos | `usuarios`, `hortas`, `plantas`, `plantios`, `tempo` e `saude`, ligadas por referências (`ObjectId`) e por chave natural (`plantas.estacoes` → `tempo.codigo`) |
| Dados complexos | `plantas` (documentos `rega` e `apartamento` aninhados + listas `janelasPlantio`, `cuidados`, `estacoes`), `hortas` (`canteiros`), `plantios` (`regas`, `cuidados`), `tempo` (`meses`), `saude` (listas `evitar` e `recomendadas` com referências às plantas) |
| PoC executável | Menu de terminal com cadastro, hortas, plantios, rega, cuidados, tarefas do dia, colheita e modo demonstração |
| Testes automatizados | JUnit 5: mais de 200 testes unitários/de fluxo + testes de integração com MongoDB real |
| Cobertura mínima 70% | JaCoCo; `mvn verify` **falha** se a cobertura de linhas ficar abaixo de 70% |
| GitHub | Repositório Git organizado + CI (GitHub Actions) com MongoDB |
| Documentação | Este README e `docs/` |

## Modelo de dados (resumo)

```
usuarios 1 ──< hortas 1 ──< plantios >── 1 plantas >──< tempo   (plantas.estacoes[] = tempo.codigo)
                │               │            │  ▲            │
         canteiros[]       regas[]      rega{}  │      meses[] + melhor mês
         (embutidos)   cuidados[]  apartamento{} │
                          (embutidos) janelasPlantio[] cuidados[]
                                                 │
                       saude (gatos, cães, pessoas, comorbidades)
                       evitar[] / recomendadas[] ── plantaId ──> plantas
```

Detalhes, diagrama e exemplos de documentos: [`docs/MODELO_DE_DADOS.md`](docs/MODELO_DE_DADOS.md).
Consultas prontas para o `mongosh`: [`docs/consultas-mongosh.js`](docs/consultas-mongosh.js).
Fontes, avisos de saúde e como ampliar o catálogo: [`docs/FONTES_E_AVISOS.md`](docs/FONTES_E_AVISOS.md).

## Catálogo, estações e saúde

| Recurso | Como usar no terminal |
|---|---|
| **240+ plantas** | Menu **2 – Catálogo**: buscar por nome (sem diferenciar acentos), filtrar por categoria, estação, luz ou apartamento; listas paginadas |
| **Luz solar** | Cada planta tem um nível: **pouca luz** (até ~3 h de sol), **luz média** (meio dia de sol, 4–5 h) ou **sol pleno** (o dia todo, 6 h+) |
| **Apartamento** | **Ideal**, **possível com cuidados** ou **não recomendado**, com observação. Menu 2 → *Plantas para apartamento* (combina com o filtro de luz) |
| **Coleção `tempo`** | Menu **3 – Estações do ano**: as 4 estações, **o melhor mês** de cada uma (com justificativa) e quantas plantas podem ser plantadas em cada mês |
| **Estação nas plantas** | A ficha de cada planta mostra as estações em que ela pode ser plantada e o melhor mês |
| **Coleção `saude`** | Menu **5 – Saúde e segurança**: plantas perigosas para **gatos**, **cães** e **pessoas**; e, por **condição de saúde** (problemas respiratórios, alergia a pólen, pressão alta, diabetes, doença renal, anticoagulante, gravidez/amamentação, gastrite/refluxo, tireoide, fígado), o que **recomendar** e o que **evitar** |
| **Alertas na hora de plantar** | Ao iniciar um plantio, o sistema avisa se a planta é tóxica para pets ou pessoas |

> ⚠️ **Aviso:** as informações de saúde são **educativas**, compiladas de fontes públicas, e **não substituem**
> médico, nutricionista ou veterinário. Se um animal comer uma planta suspeita, procure um veterinário na hora.

## Instalação

### Pré-requisitos
- **Java 17+** (`java -version`)
- **Maven 3.8+** (`mvn -version`) — a extensão *Extension Pack for Java* do VSCode já traz um Maven embutido
- **MongoDB 6+** acessível (escolha uma opção):
  - **Docker (mais simples):** `docker compose up -d` (usa o `docker-compose.yml` do projeto)
  - **Instalação local:** [MongoDB Community Server](https://www.mongodb.com/try/download/community) rodando na porta 27017
  - **MongoDB Atlas (nuvem):** defina `MONGODB_URI` com a string de conexão do cluster

### Configuração (opcional)
| Variável | Padrão | Uso |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017` | endereço do MongoDB |
| `MONGODB_DB` | `ecohorta` | nome do banco |

Exemplo (PowerShell): `$env:MONGODB_URI="mongodb+srv://usuario:senha@cluster.mongodb.net"`
Exemplo (Linux/macOS): `export MONGODB_URI="mongodb://localhost:27017"`

## Execução

No terminal do VSCode, na pasta do projeto:

```bash
mvn -q compile exec:java
```

Ou gerando um JAR executável:

```bash
mvn -q package -DskipTests
java -jar target/ecohorta.jar
```

Na primeira execução o catálogo (240+ plantas), as 4 estações e os 13 registros de saúde são criados automaticamente (sem duplicar nas próximas).

> **Windows:** se acentos/emojis aparecerem errados, rode `chcp 65001` antes de executar.

### Roteiro rápido de demonstração
1. No menu inicial escolha **6 – Carregar dados de demonstração e entrar**
   (cria usuário, horta com 2 canteiros e 5 plantios com histórico).
2. **8 – Tarefas de hoje**: veja regas, cuidados e colheitas pendentes/atrasadas.
3. **6 – Registrar rega** e **7 – Registrar cuidado**: as tarefas correspondentes somem e a próxima data é recalculada.
4. **5 – Ver plantios**, **9 – Encerrar plantio** (colhido/perdido) e **10 – Resumo da horta**.
5. Para o fluxo "do zero": **1 – Entrar/cadastrar** com seu e-mail → criar horta → adicionar canteiro → iniciar plantio.

O menu **4 – O que plantar neste mês?** usa a data atual para sugerir plantas na época certa.

Para explorar as novidades sem precisar entrar: **2 → 4** (plantas ideais para apartamento, filtrando por luz),
**3** (melhor mês de cada estação) e **5 → 1** (plantas perigosas para gatos).

## Testes e cobertura — segunda entrega

Requisitos: JDK 17 e Maven 3.8 ou superior. Para a medição completa, inicie o MongoDB:

```bash
docker compose up -d
mvn clean verify -Dmongo.obrigatorio=true
```

- **Resultado validado em 13/11/2026:** 220 testes aprovados, sem falhas ou erros,
  incluindo 16 testes de integração com MongoDB real.
- **Cobertura de linhas: 98,20%** (1.907 de 1.942 linhas executáveis), calculada sobre
  todo o código de produção, sem exclusões de classes ou pacotes.
- O JaCoCo mantém o mínimo de **70% de linhas** e reprova `mvn verify` abaixo desse valor.
- Relatório desta versão: [docs/cobertura/index.html](docs/cobertura/index.html).
  Baixe o projeto e abra esse arquivo no navegador; o GitHub exibe o código HTML.
- Ao executar novamente, o relatório é gerado em `target/site/jacoco/index.html`,
  com versões XML e CSV na mesma pasta.
- Os testes de integração usam um banco temporário próprio, apagado ao final.
  `MONGODB_URI` permite indicar outro servidor.
- `-Dmongo.obrigatorio=true` reprova a execução se o banco estiver indisponível.
  A CI usa essa opção e aguarda a verificação de saúde do MongoDB.

Para testar localmente sem exigir MongoDB:

```bash
mvn clean verify
```

Sem servidor acessível, a suíte de integração é abortada por uma condição do JUnit.
Nesse cenário foram aprovados **204 testes unitários e de fluxo**, com **89,70%** de
cobertura de linhas. Os testes de fluxo usam repositórios em memória com os mesmos
mapeadores BSON da aplicação e relógio fixo em 10/11/2026.

Detalhes dos cenários e da medição: [docs/TESTES_E_COBERTURA.md](docs/TESTES_E_COBERTURA.md).

## Estrutura do projeto

```
EcoHorta/
├── pom.xml                      # Maven: driver Mongo, JUnit 5, JaCoCo (mín. 70%)
├── docker-compose.yml           # MongoDB local
├── .github/workflows/ci.yml     # CI: build + testes + cobertura (com MongoDB)
├── docs/                        # modelo de dados, consultas mongosh, fontes e avisos
└── src/
    ├── main/resources/catalogo/plantas.csv   # 237 plantas (editável: ver docs/FONTES_E_AVISOS.md)
    ├── main/java/com/ecohorta/
    │   ├── Main.java            # monta dependências e inicia o menu
    │   ├── config/              # conexão com o MongoDB
    │   ├── model/               # entidades e objetos de valor (domínio)
    │   ├── repository/          # interfaces de persistência
    │   │   └── mongo/           # implementação MongoDB + mappers objeto <-> BSON + índices
    │   ├── service/             # regras de negócio (plantios, catálogo, tempo, saúde) e dados iniciais
    │   ├── cli/                 # menu de terminal (só entrada/saída)
    │   └── demo/                # dados de demonstração
    └── test/java/com/ecohorta/  # testes (espelham os pacotes) + support/ (repos em memória)
```

## Decisões de projeto
- **Referências vs. embutidos:** dados que nascem e morrem junto com o pai (canteiros, regas, cuidados,
  janelas de plantio) ficam **embutidos**; entidades com vida própria (usuário, horta, planta, plantio)
  ficam em **coleções separadas ligadas por `ObjectId`**.
- **Repository pattern:** as regras de negócio dependem de interfaces; o MongoDB é um detalhe de infraestrutura
  (por isso os testes rodam sem banco).
- **Datas** são gravadas como `Date` BSON (meia-noite UTC), permitindo consultas por período no MongoDB.
- **Índices:** e-mail de usuário, nome de planta, `tempo.codigo` e `saude.codigo` únicos; `plantas.estacoes`, `categoria`,
  `nivelLuz`, `apartamento.nivel`, `saude.evitar.plantaId`, `saude.recomendadas.plantaId`, `hortaId`, `usuarioId` e `plantaId` indexados.
- **Campos derivados gravados:** `nomeBusca` (sem acentos), `estacoes`, `melhorMesPlantio` e `horasSolMinimas` são calculados
  na gravação para permitir consultas e índices sem processar todo o catálogo.
- **Estações por mês inteiro:** primavera set–nov, verão dez–fev, outono mar–mai, inverno jun–ago (hemisfério sul).
- **Dados agronômicos** do catálogo são indicativos para o Centro-Sul do Brasil e devem ser validados
  conforme clima e variedade.

## Subindo para o GitHub

O repositório já vem com histórico Git local (branch `main`). Para publicar:

```bash
# crie um repositório vazio no GitHub e então:
git remote add origin https://github.com/SEU-USUARIO/ecohorta.git
git push -u origin main
```

O workflow `.github/workflows/ci.yml` passa a rodar automaticamente a cada push/PR.

## Limitações e próximos passos
- Sem autenticação por senha (o "login" é só pelo e-mail, suficiente para a PoC).
- Rega não considera chuva/clima; poderia integrar previsão do tempo.
- O calendário é do Centro-Sul do Brasil; Norte/Nordeste exigiriam outro conjunto de janelas e estações (seca/chuva).
- Os dados de saúde cobrem as plantas do catálogo, não todas as espécies; "sem alerta" não significa "seguro".
- Front-end (web/mobile) consumindo uma API REST sobre os mesmos serviços.
- Notificações (e-mail/push) das tarefas do dia e mais plantas no catálogo.
