# Testes e cobertura — segunda entrega

Medição realizada em 13/11/2026 sobre o código desta versão, após a limpeza dos comentários.

## Regra da entrega

A segunda entrega exige testes automatizados pertinentes e executáveis, cobertura mínima de
70% sobre o código apresentado e evidência reproduzível. O projeto usa JUnit 5.10.2 e
JaCoCo 0.8.11. O critério configurado é **LINE / COVEREDRATIO ≥ 0,70**, aplicado ao conjunto
completo de classes de produção compiladas de `src/main/java`, sem exclusões manuais.
Código de teste e dependências externas não entram no denominador. Arquivos de dados CSV
não são código executável; sua leitura e consistência são verificadas por testes.

## Resultado

| Execução | Testes aprovados | Linhas cobertas | Cobertura de linhas |
| --- | ---: | ---: | ---: |
| Completa, com MongoDB | 220 (204 unitários/de fluxo + 16 de integração) | 1.907 / 1.942 | 98,20% |
| Sem MongoDB | 204; suíte de integração não executada | 1.742 / 1.942 | 89,70% |

Ambas as execuções concluíram `verify` com sucesso, sem falhas nem erros. A execução completa
teve zero testes ignorados. Também foi verificado que o modo obrigatório falha quando o
MongoDB está indisponível, sem apresentar a integração como aprovada.

Na execução completa, a cobertura de instruções foi 98,66% (12.285/12.452) e a de ramos
94,28% (659/699). Essas métricas são informativas; o bloqueio de 70% permanece em linhas.

### Cobertura de linhas por pacote (com MongoDB)

| Pacote | Linhas cobertas / total | Cobertura |
| --- | ---: | ---: |
| `com.ecohorta` | 0/22 | 0.00% |
| `com.ecohorta.cli` | 538/545 | 98.72% |
| `com.ecohorta.config` | 14/17 | 82.35% |
| `com.ecohorta.demo` | 26/26 | 100.00% |
| `com.ecohorta.model` | 423/423 | 100.00% |
| `com.ecohorta.repository` | 8/8 | 100.00% |
| `com.ecohorta.repository.mongo` | 393/393 | 100.00% |
| `com.ecohorta.service` | 505/508 | 99.41% |

O ponto de entrada `Main` continua incluído no cálculo, com 0% de cobertura. Há também
caminhos de erro e alternativas de menu não exercitados. Os testes não demonstram
cobertura total, nem substituem a validação manual da aplicação e dos demais entregáveis.

## Cenários contemplados

- Modelo: validações de campos, janelas de plantio, datas, estados, regas, cuidados,
  estações, luz, apartamento e registros de saúde.
- Catálogo: carga do CSV, nomes normalizados, dados inválidos, categorias, filtros
  combinados, indicação por mês e idempotência da carga inicial.
- Serviços: usuários, hortas, canteiros, plantios, tarefas, colheita, perda, estações e
  perfis de saúde, com resultados e exceções verificados.
- Terminal: entradas inválidas, navegação, paginação, consulta de plantas, saúde,
  demonstração e fluxo de cadastro até colheita.
- MongoDB: persistência e leitura, subdocumentos, listas, referências, agregações,
  `$lookup`, índices únicos e atualização sem duplicação.

### Complementos desta revisão

1. Filtro simultâneo por termo, categoria, estação, luz, apartamento e comestibilidade,
   incluindo combinações sem resultados e as duas variedades de rúcula do catálogo atual.
2. Busca literal de metacaracteres, evitando que `.*` seja interpretado como expressão regular.
3. Consultas com identificadores nulos, inválidos e válidos sem documento correspondente.
4. Atualização de estação e meses preservando a identidade do documento.
5. Consulta de saúde pela lista de recomendadas e atualização das listas sem duplicação.
6. Unicidade do nome de planta mesmo com diferenças de acentos e letras maiúsculas.
7. Cuidado periódico padrão para a categoria `OUTRA`.

## Como reproduzir

Ambiente usado: JDK 17.0.13, Maven 3.9.9 e MongoDB 7.0.16.
Na raiz do projeto, com JDK 17, Maven e Docker Compose instalados:

```bash
docker compose up -d
mvn clean verify -Dmongo.obrigatorio=true
```

Aguarde o MongoDB ficar acessível antes do comando Maven. O servidor padrão é
`mongodb://localhost:27017`; outra instância pode ser indicada por `MONGODB_URI`.
A suíte cria um banco `ecohorta_teste_<timestamp>` e o remove ao finalizar. Utilize uma
instância de desenvolvimento/testes que permita criar índices e remover esse banco temporário.

- HTML atualizado: `target/site/jacoco/index.html`.
- XML e CSV: `target/site/jacoco/jacoco.xml` e `target/site/jacoco/jacoco.csv`.
- Resultados JUnit: `target/surefire-reports/`.
- Evidência da execução completa desta revisão: [cobertura/index.html](cobertura/index.html).

Para execução sem exigir MongoDB, use `mvn clean verify`, sem a propriedade obrigatória.
Na CI, o MongoDB possui healthcheck e o comando usa a propriedade obrigatória; falhas do
banco reprovam a execução. O relatório JaCoCo é publicado como artefato da CI.

## Escopo da alteração

Foram complementados os testes, ajustada a validação de integração na CI e atualizada a
documentação da cobertura. Foram removidos os comentários de Java, do script de consultas
MongoDB e das configurações Maven/CI/Compose. A sequência de tokens executáveis dos arquivos
de produção foi comparada com a versão recebida e permaneceu igual. Os textos e dados da
aplicação, suas funcionalidades e as regras de negócio foram preservados.
