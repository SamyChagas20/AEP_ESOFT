# 🌱 EcoHorta

**EcoHorta** é uma aplicação em Java voltada para o gerenciamento e monitoramento de hortas sustentáveis e cultivo de plantas. O projeto foi desenvolvido no âmbito acadêmico (Atividade Estruturada Prática - AEP) para o curso de Engenharia de Software.

---

## 🚀 Tecnologias Utilizadas

- **Linguagem:** Java (JDK 17+)
- **Gerenciador de Dependências:** Apache Maven
- **Banco de Dados:** MongoDB (NoSQL)
- **Testes Unitários:** JUnit 5

---

## 🛠️ Arquitetura do Projeto

O projeto adota uma estrutura em camadas para garantir a separação clara de responsabilidades:

EcoHorta/
 ├── src/
 │    ├── main/
 │    │    └── java/com/ecohorta/
 │    │         ├── db/           # Conexão e gerenciamento do MongoDB (MongoConnection)
 │    │         ├── model/        # Entidades/Modelos do sistema (Planta)
 │    │         ├── dao/          # Objeto de Acesso a Dados (PlantaDAO)
 │    │         ├── service/      # Camada de Regras de Negócio (HortaService)
 │    │         └── Main.java     # Classe principal / Ponto de entrada da aplicação
 │    └── test/
 │         └── java/com/ecohorta/
 │              └── model/        # Suíte de testes unitários (EcoHortaTest)
 ├── pom.xml                      # Gerenciamento de dependências Maven
 └── README.md

 📌 Funcionalidades Principais
Gerenciamento de Plantas: Cadastro, alteração, consulta e remoção das plantas cultivadas.

Integração NoSQL: Comunicação com MongoDB para persistência rápida e flexível.

Serviços de Horta: Módulo de regras de negócio para monitoramento e gestão da horta.

Garantia de Qualidade: Testes unitários para verificação do comportamento das entidades e rotinas do sistema.

🔧 Como Executar o Projeto
Pré-requisitos
JDK 17 ou superior instalado e configurado nas variáveis de ambiente.

Apache Maven instalado.

Instância ativa do MongoDB (local ou cluster no MongoDB Atlas).
