<a id="top"></a>
<div align="center">

# 🚀 Astronaut Health API (`astronaut-health-api`)

**Core Telemetry Gateway, Clinical Orchestration & Persistence Microservice — Team Rapadura Cósmica**

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![H2 Database](https://img.shields.io/badge/H2_Database-In_Memory-0000BB?style=for-the-badge&logo=databricks&logoColor=white)](https://www.h2database.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

<p>
  <a href="#english-docs">🇺🇸 English</a> •
  <a href="#portugues-docs">🇧🇷 Português</a>
</p>

</div>

---

<a id="english-docs"></a>
## 🇺🇸 English

### Overview

This microservice is the **Core Telemetry Gateway, Clinical Orchestration & Persistence Engine** (`astronaut-health-api`) developed by **Team Rapadura Cósmica** for the **NASA Space Apps Challenge 2026**.

Operating as the central integration hub within a decoupled microservice architecture, it ingests raw astronaut biometric and environmental payloads from edge simulators or wearable hardware, delegates real-time clinical triage and operational protocol generation to the **Python Analytics Engine** (`telemetry-analytics`), persists enriched records in an **H2 Database**, and serves live telemetry to the **Mission Control UI** (`mission-control-ui`).

### Key Responsibilities

* **Telemetry Ingestion:** Receives raw biometric JSON payloads from edge simulators or wearable sensors via `POST /api/telemetry`.
* **Clinical Orchestration:** Forwards incoming payloads over HTTP to `telemetry-analytics` (`POST /api/v1/analyze`) for schema validation, risk level classification, and `recommendedAction` protocol generation.
* **Resilient Fallback:** Gracefully handles network failures or analytics service downtime by applying a safe fallback classification (`riskLevel: "UNKNOWN"`) so telemetry persistence is never interrupted.
* **Persistence & History:** Stores enriched biometric records (`riskLevel`, `alertMessage`, `recommendedAction`) in an in-memory **H2 Database** via Spring Data JPA.
* **Mission Control Exposure:** Serves the latest reading (`GET /api/telemetry/latest`) and full telemetry history (`GET /api/telemetry`) with global CORS enabled for real-time dashboard polling.

### System Flow

```text
[Telemetry Simulator] ──(POST /api/telemetry)──► [Core API: Spring Boot :8080] ──► [H2 Database]
                                                            │          │
                                  (GET /api/telemetry/latest)          └──(POST /api/v1/analyze)──► [Flask Analytics Engine :5000]
                                            ▼                                                               │
                                   [Mission Dashboard] ◄───────────────(200 OK: Enriched JSON)────────────┘
```

### Project Architecture

Structured following Spring Boot layered architecture best practices to ensure clean separation between HTTP controllers, external HTTP clients, domain persistence, and business orchestration:

```text
astronaut-health-api/
├── src/
│   ├── main/
│   │   ├── java/com/rapaduracosmica/astronauthealthapi/
│   │   │   ├── client/
│   │   │   │   └── AnalyticsClient.java            # HTTP client calling telemetry-analytics (:5000)
│   │   │   ├── controller/
│   │   │   │   └── TelemetryController.java        # REST endpoints (/api/telemetry & /latest)
│   │   │   ├── dto/
│   │   │   │   └── TelemetryAnalysisDTO.java       # Unified camelCase JSON contract DTO
│   │   │   ├── model/
│   │   │   │   └── TelemetryRecord.java            # JPA Entity mapped to TB_TELEMETRIA
│   │   │   ├── repository/
│   │   │   │   └── TelemetryRepository.java        # Spring Data JPA persistence interface
│   │   │   ├── service/
│   │   │   │   └── TelemetryService.java           # Orchestration: ingest -> analyze -> persist
│   │   │   └── AstronauthealthapiApplication.java  # Spring Boot entry point
│   │   └── resources/
│   │       └── application.yaml                    # H2 datasource, JPA & H2 console settings
│   └── test/                                       # JUnit 5 & Spring Data JPA repository tests
├── Dockerfile                                      # Multi-stage Maven + JRE container build
├── pom.xml                                         # Maven dependencies & build configuration
└── README.md                                       # Bilingual project documentation
```

### API Contract

#### 1. Telemetry Ingestion & Enrichment
* **Endpoint:** `POST /api/telemetry`
* **Request Payload (from Simulator / Hardware):**
```json
{
  "astronautId": "ASTRO-01",
  "timestamp": "2026-11-14T15:30:00Z",
  "heartRate": 118,
  "spo2": 96,
  "radiationDoseMsv": 0.4,
  "sleepHours": 7.2
}
```
* **Response Payload (`201 Created`):**
```json
{
  "id": 1,
  "astronautId": "ASTRO-01",
  "timestamp": "2026-11-14T15:30:00Z",
  "heartRate": 118,
  "spo2": 96,
  "radiationDoseMsv": 0.4,
  "sleepHours": 7.2,
  "riskLevel": "WARNING",
  "alertMessage": "Frequência cardíaca elevada/baixa detectada.",
  "recommendedAction": "Reduzir carga física, reforçar hidratação e reavaliar sinais vitais em 15 minutos."
}
```

#### 2. Latest Telemetry Retrieval
* **Endpoint:** `GET /api/telemetry/latest`
* **Optional Query Parameter:** `?astronautId=ASTRO-01`
* **Response (`200 OK`):** Returns the most recent classified `TelemetryRecord` JSON (or `404 Not Found` if no records exist yet).

#### 3. Telemetry History
* **Endpoint:** `GET /api/telemetry`
* **Response (`200 OK`):** Returns the complete list of persisted telemetry records.

### Configuration (`application.yaml`)

```yaml
spring:
  application:
    name: astronauthealthapi

  datasource:
    url: jdbc:h2:mem:astronautdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ''

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true

  h2:
    console:
      enabled: true
      path: /h2-console
```

### Getting Started

#### Option 1: Full Platform via Docker Compose (Recommended)

Run the entire stack (`telemetry-analytics`, `astronaut-health-api`, and `mission-control-ui`) from the central platform orchestrator:

```bash
# 1. Clone the platform repository with submodules
git clone --recurse-submodules https://github.com/rapadura-cosmica-nasa/rapadura-cosmica-platform.git
cd rapadura-cosmica-platform

# 2. Build and start all containers
docker compose up --build -d
```

#### Option 2: Standalone Local Execution

```bash
# 1. Clone the repository
git clone https://github.com/rapadura-cosmica-nasa/astronaut-health-api.git
cd astronaut-health-api

# 2. Build and run the Spring Boot application (Port 8080)
./mvnw spring-boot:run

# 3. Run unit and repository tests
./mvnw test
```

* **API Base URL:** `http://localhost:8080`
* **H2 Database Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:astronautdb` | User: `sa` | Password: *(empty)*)

---

<a id="portugues-docs"></a>
## 🇧🇷 Português

<p align="right">
  <a href="#top">⬆️ Back to top / Voltar ao topo</a>
</p>

### Visão Geral

Este microsserviço é o **Gateway Central de Telemetria, Orquestração Clínica e Persistência** (`astronaut-health-api`) desenvolvido pela equipe **Rapadura Cósmica** para o **NASA Space Apps Challenge 2026**.

Operando como o núcleo de integração dentro de uma arquitetura de microsserviços desacoplados, ele ingere pacotes biométricos e ambientais brutos vindos de simuladores ou hardware vestível, delega a triagem clínica e geração de protocolos operacionais em tempo real ao **Motor Analítico Python** (`telemetry-analytics`), persiste os registros enriquecidos em um **Banco H2** e disponibiliza os dados ao vivo para o **Mission Control UI** (`mission-control-ui`).

### Principais Responsabilidades

* **Ingestão de Telemetria:** Recebe payloads biométricos brutos enviados pelo simulador ou hardware via `POST /api/telemetry`.
* **Orquestração Clínica:** Encaminha os dados recebidos via HTTP para o `telemetry-analytics` (`POST /api/v1/analyze`) para validação de contrato, classificação de risco e geração de diretrizes (`recommendedAction`).
* **Fallback Resiliente:** Trata falhas de rede ou indisponibilidade do motor Python aplicando uma classificação segura (`riskLevel: "UNKNOWN"`), garantindo que nenhuma leitura seja perdida.
* **Persistência & Histórico:** Armazena os registros biométricos já enriquecidos (`riskLevel`, `alertMessage` e `recommendedAction`) em banco **H2** via Spring Data JPA.
* **Exposição para o Painel:** Disponibiliza a última leitura (`GET /api/telemetry/latest`) e o histórico completo (`GET /api/telemetry`) com CORS habilitado para atualização contínua do dashboard.

### Fluxo no Sistema

```text
[Simulador de Telemetria] ──(POST /api/telemetry)──► [Core API: Spring Boot :8080] ──► [Banco H2]
                                                            │          │
                                  (GET /api/telemetry/latest)          └──(POST /api/v1/analyze)──► [Motor Flask Analytics :5000]
                                            ▼                                                               │
                                   [Painel da Missão] ◄───────────────(200 OK: JSON Enriquecido)────────────┘
```

### Arquitetura do Projeto

Estruturado seguindo as boas práticas de arquitetura em camadas do Spring Boot para garantir separação clara entre controladores REST, clientes HTTP externos, persistência JPA e regras de orquestração:

```text
astronaut-health-api/
├── src/
│   ├── main/
│   │   ├── java/com/rapaduracosmica/astronauthealthapi/
│   │   │   ├── client/
│   │   │   │   └── AnalyticsClient.java            # Cliente HTTP que consome telemetry-analytics (:5000)
│   │   │   ├── controller/
│   │   │   │   └── TelemetryController.java        # Endpoints REST (/api/telemetry e /latest)
│   │   │   ├── dto/
│   │   │   │   └── TelemetryAnalysisDTO.java       # Contrato JSON unificado em camelCase
│   │   │   ├── model/
│   │   │   │   └── TelemetryRecord.java            # Entidade JPA mapeada para TB_TELEMETRIA
│   │   │   ├── repository/
│   │   │   │   └── TelemetryRepository.java        # Interface de persistência Spring Data JPA
│   │   │   ├── service/
│   │   │   │   └── TelemetryService.java           # Orquestração: recebe -> analisa -> persiste
│   │   │   └── AstronauthealthapiApplication.java  # Ponto de entrada da aplicação Spring Boot
│   │   └── resources/
│   │       └── application.yaml                    # Configurações do H2, JPA e Console Web
│   └── test/                                       # Testes automatizados JUnit 5 e Spring Data JPA
├── Dockerfile                                      # Build multi-stage com Maven + JRE
├── pom.xml                                         # Dependências e configuração de build Maven
└── README.md                                       # Documentação bilíngue do projeto
```

### Contrato da API

#### 1. Ingestão e Enriquecimento de Telemetria
* **Endpoint:** `POST /api/telemetry`
* **Payload de Entrada (enviado pelo Simulador / Hardware):**
```json
{
  "astronautId": "ASTRO-01",
  "timestamp": "2026-11-14T15:30:00Z",
  "heartRate": 118,
  "spo2": 96,
  "radiationDoseMsv": 0.4,
  "sleepHours": 7.2
}
```
* **Payload de Saída (`201 Created`):**
```json
{
  "id": 1,
  "astronautId": "ASTRO-01",
  "timestamp": "2026-11-14T15:30:00Z",
  "heartRate": 118,
  "spo2": 96,
  "radiationDoseMsv": 0.4,
  "sleepHours": 7.2,
  "riskLevel": "WARNING",
  "alertMessage": "Frequência cardíaca elevada/baixa detectada.",
  "recommendedAction": "Reduzir carga física, reforçar hidratação e reavaliar sinais vitais em 15 minutos."
}
```

#### 2. Consulta da Última Leitura
* **Endpoint:** `GET /api/telemetry/latest`
* **Parâmetro Opcional:** `?astronautId=ASTRO-01`
* **Resposta (`200 OK`):** Retorna o JSON do registro `TelemetryRecord` mais recente (ou `404 Not Found` caso o banco ainda esteja vazio).

#### 3. Histórico de Telemetria
* **Endpoint:** `GET /api/telemetry`
* **Resposta (`200 OK`):** Retorna a lista completa de registros persistidos no banco.

### Configuração (`application.yaml`)

```yaml
spring:
  application:
    name: astronauthealthapi

  datasource:
    url: jdbc:h2:mem:astronautdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ''

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true

  h2:
    console:
      enabled: true
      path: /h2-console
```

### Como Executar

#### Opção 1: Plataforma Completa via Docker Compose (Recomendado)

Suba toda a arquitetura (`telemetry-analytics`, `astronaut-health-api` e `mission-control-ui`) a partir do repositório orquestrador central:

```bash
# 1. Clone o repositório da plataforma com os submódulos
git clone --recurse-submodules https://github.com/rapadura-cosmica-nasa/rapadura-cosmica-platform.git
cd rapadura-cosmica-platform

# 2. Construa e inicie todos os contêineres
docker compose up --build -d
```

#### Opção 2: Execução Local Isolada

```bash
# 1. Clone o repositório
git clone https://github.com/rapadura-cosmica-nasa/astronaut-health-api.git
cd astronaut-health-api

# 2. Compile e inicie a aplicação Spring Boot (Porta 8080)
./mvnw spring-boot:run

# 3. Execute a suíte de testes unitários e de repositório
./mvnw test
```

* **URL Base da API:** `http://localhost:8080`
* **Console do Banco H2:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:astronautdb` | Usuário: `sa` | Senha: *(em branco)*)

---

<div align="center">

**Team Rapadura Cósmica** — NASA Space Apps Challenge 2026

* **Core API & Persistência (Java / Spring Boot):** [Mateus Queiroz](https://github.com/tue1093) & [Grimaldo Júnior](https://github.com/grimaldojunior0413-cloud)
* **Edge, Simulação & Análise de Anomalias (Python / Flask):** [Murilo Costa](https://github.com/MuriloDev07) & [Gabriel Guedes](https://github.com/gueeds)
* **Interface & Experiência do Astronauta (Front-end):** [Cauã Max](https://github.com/pl4utus)
* **Pesquisa Científica NASA, Documentação & Pitch:** [Davi Duclerc](https://github.com/daviduclerc)

<a href="#top">⬆️ Back to top / Voltar ao topo</a>

</div>
