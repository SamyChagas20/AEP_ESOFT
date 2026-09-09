# 🌱 EcoHorta — Protótipo (Terminal)

Protótipo em Java que ajuda a cuidar de uma horta, indicando **época de
plantio**, **frequência de rega** e **cuidados** de 5 plantas: Tomate,
Cebolinha, Pimenta, Alface e Salsinha.

Sem front-end por enquanto — tudo roda pelo terminal. Os dados ficam
armazenados no **MongoDB**.

---

## 1. Pré-requisitos

1. **Java 17+** instalado (`java -version` no terminal).
2. **Maven** instalado (`mvn -version` no terminal).
   - No VSCode, a extensão *"Extension Pack for Java"* já traz Maven embutido.
3. **MongoDB** instalado e rodando localmente na porta padrão (27017).
   - Instale o MongoDB Community Server: https://www.mongodb.com/try/download/community
   - Depois de instalado, garanta que o serviço está rodando:
     - Windows: o serviço "MongoDB" já inicia sozinho (verifique em "Serviços").
     - Mac: `brew services start mongodb-community`
     - Linux: `sudo systemctl start mongod`
   - Alternativa sem instalar nada: crie um cluster grátis no **MongoDB
     Atlas** e troque a `CONNECTION_STRING` em
     `src/main/java/com/ecohorta/db/MongoConnection.java` pela string de
     conexão do Atlas.

## 2. Abrindo o projeto no VSCode

1. Abra a pasta `EcoHorta` no VSCode (`File > Open Folder...`).
2. Instale a extensão **"Extension Pack for Java"** (se ainda não tiver).
3. O VSCode vai reconhecer automaticamente o projeto Maven (`pom.xml`).

## 3. Rodando o protótipo

Abra o terminal integrado do VSCode (`Ctrl + '` ou `Terminal > New Terminal`)
dentro da pasta `EcoHorta` e rode:

```bash
mvn compile exec:java
```

Isso vai:
- Baixar as dependências (na primeira vez).
- Conectar ao MongoDB local.
- Criar automaticamente o banco `ecohorta` e a coleção `plantas`, populando
  com as 5 plantas iniciais (só na primeira execução — depois disso ele
  detecta que já existem dados e não duplica).
- Abrir o menu interativo no terminal.

### Alternativa: gerar um .jar executável

```bash
mvn package
java -jar target/ecohorta.jar
```

## 4. Usando o menu

```
1 - Listar todas as plantas
2 - Ver detalhes de uma planta
3 - Registrar rega de hoje
4 - Ver plantas que precisam de rega hoje
5 - Cadastrar nova planta
0 - Sair
```

## 5. Estrutura do projeto

```
EcoHorta/
├── pom.xml
├── README.md
└── src/main/java/com/ecohorta/
    ├── Main.java                 -> menu do terminal
    ├── model/Planta.java         -> classe que representa uma planta
    ├── db/MongoConnection.java   -> conexão com o MongoDB
    ├── dao/PlantaDAO.java        -> CRUD (insere, lista, busca, atualiza)
    └── service/HortaService.java -> regras de negócio + cadastro inicial
```

## 6. Próximos passos sugeridos

- Front-end (web ou mobile) consumindo os mesmos dados via uma API REST.
- Notificações reais (e-mail/push) quando uma planta precisar de rega.
- Suporte a múltiplas hortas por usuário (login).
- Fotos e histórico de crescimento de cada planta.
