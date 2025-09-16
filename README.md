# Inventory Core Service

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-green.svg)](https://swagger.io/specification/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-orange.svg)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

Sistema de gerenciamento de eventos de inventário com arquitetura orientada a eventos, integração RabbitMQ com Dead Letter Queue (DLQ), processamento assíncrono e API REST completa.

## 📋 Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [API Documentation](#api-documentation)
- [Mensageria e DLQ](#mensageria-e-dlq)
- [Funcionalidades](#funcionalidades)
- [Exemplos de Uso](#exemplos-de-uso)
- [Monitoramento](#monitoramento)
- [Estrutura do Projeto](#estrutura-do-projeto)

## 🎯 Sobre o Projeto

O **Inventory Core Service** é um microserviço robusto responsável por gerenciar eventos de inventário em tempo real. O sistema processa mudanças de estoque através de uma arquitetura orientada a eventos, garantindo rastreabilidade completa, processamento assíncrono e recuperação de falhas.

### Principais Características

- ✅ **Event Sourcing**: Todos os eventos são persistidos para auditoria completa
- ✅ **Processamento Assíncrono**: Integração com RabbitMQ com padrão Publisher/Subscriber
- ✅ **Dead Letter Queue (DLQ)**: Tratamento e recuperação de mensagens falhadas
- ✅ **API REST**: Endpoints documentados com OpenAPI 3.0/Swagger
- ✅ **Simulação de Falhas**: Ambiente configurável para testes de resiliência
- ✅ **Migração de Dados**: Endpoint para migração de inventário legado
- ✅ **Docker Ready**: Containerização completa com Docker Compose
- ✅ **Monitoramento**: Health checks e métricas integradas
- ✅ **Validações Robustas**: Validação de dados com Bean Validation

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   RabbitMQ      │    │   REST API      │    │   H2 Database   │
│   (Messaging)   │◄──►│   (Commands)    │◄──►│   (Events)      │
│   + DLQ         │    │   Port: 8081    │    │   In-Memory     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         ▼                        ▼                        ▼
┌─────────────────────────────────────────────────────────────��───┐
│                Inventory Core Service                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │ Publisher   │  │ Controller  │  │ Event Store │           │
│  │ Subscriber  │  │ (REST API)  │  │ (JPA)       │           │
│  │ DLQ Handler │  │ Swagger UI  │  │ Repository  │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

## 🛠️ Tecnologias Utilizadas

### Core Technologies
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.5** - Framework principal
- **Spring AMQP** - Integração com RabbitMQ
- **Spring Data JPA** - Persistência de dados
- **Maven** - Gerenciamento de dependências

### Database & Messaging
- **H2 Database** - Banco em memória para desenvolvimento
- **RabbitMQ** - Message broker para comunicação assíncrona
- **Jackson** - Serialização JSON

### Documentation & Monitoring
- **OpenAPI 3.0 / Swagger UI** - Documentação da API
- **Spring Boot Actuator** - Monitoramento e métricas
- **Lombok** - Redução de boilerplate
- **MapStruct** - Mapeamento de objetos

## 📋 Pré-requisitos

- **Java 21+**
- **Maven 3.8+**
- **Docker** (para execução com containers)

## 🚀 Instalação e Execução

### 2. Execução Local (sem Docker)
```bash
# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Executar a aplicação
mvn spring-boot:run
```

### 3. Verificar Aplicação
- **API Base**: http://localhost:8081/api/inventory-core/v1
- **Swagger UI**: http://localhost:8081/api/inventory-core/v1/swagger-ui.html
- **H2 Console**: http://localhost:8081/api/inventory-core/v1/h2-console
- **Health Check**: http://localhost:8081/api/inventory-core/v1/actuator/health

## 📚 API Documentation

### Documentação Completa

## 📨 Mensageria e DLQ

### Configuração RabbitMQ

#### Filas Configuradas
- **Publisher Queue**: `SEND_EVENT_INVENTORY_QUEUE`
- **Subscriber Queue**: `PROCESS_UPDATE_INVENTORY_QUEUE`
- **Dead Letter Queue**: `SEND_EVENT_INVENTORY_QUEUE_DLQ`

#### Dead Letter Queue (DLQ)
O sistema implementa tratamento de falhas com DLQ:

```yaml
# Configuração no application.yaml
queue:
  simulate:
    failure: true  # Habilita simulação de falhas para testes
  publisher:
    exchange: subs-inventory-fanout
    dead-letter-exchange: subs-inventory-dlx
    dead-letter-routing-key: dlx-routing-key
```

#### Fluxo de Mensagens
1. **Publisher** envia eventos para a fila principal
2. **Subscriber** processa mensagens da fila
3. **DLQ** captura mensagens com falha para reprocessamento
4. **Simulação de Falhas** pode ser habilitada via configuração

## ⚡ Funcionalidades

### 1. Gestão de Eventos
- ✅ Criação de eventos de inventário via API
- ✅ Processamento assíncrono com RabbitMQ
- ✅ Validação de dados de entrada
- ✅ Rastreabilidade completa de eventos

### 2. Consulta de Estoque
- ✅ Consulta de estoque atual por produto
- ✅ Cálculo baseado em eventos (Event Sourcing)
- ✅ Suporte a incrementos e decrementos

### 3. Resiliência e Monitoramento
- ✅ Dead Letter Queue para tratamento de falhas
- ✅ Health checks configurados
- ✅ Métricas de aplicação
- ✅ Simulação de falhas para testes

## 📊 Monitoramento

### Health Checks
```http
GET /api/inventory-core/v1/actuator/health
```

### Métricas Disponíveis
```http
GET /api/inventory-core/v1/actuator/metrics
GET /api/inventory-core/v1/actuator/info
```

### Database Console
- **URL**: http://localhost:8081/api/inventory-core/v1/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (vazio)

## 🗂️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/mercadolibre/
│   │   ├── App.java                          # Classe principal
│   │   ├── application/                      # Camada de aplicação
│   │   │   ├── event/                       # Serviços de eventos
│   │   │   └── stock/                       # Serviços de estoque
│   │   ├── controller/                      # Controladores REST
│   │   │   ├── event/EventController.java  # API de eventos
│   │   │   └── stock/StockController.java   # API de estoque
│   │   ├── core/                           # Configurações core
│   │   │   ├── configuration/message/      # Config RabbitMQ
│   │   │   ├── constants/                  # Constantes
│   │   │   └── exception/                  # Tratamento de exceções
│   │   ├── domain/                         # Domínio da aplicação
│   │   │   └── event/                      # Entidades e serviços
│   │   └── infra/                          # Infraestrutura
│   │       ├── message/                    # Publisher/Subscriber
│   │       └── sql/                        # Repositórios JPA
│   └── resources/
│       ├── application.yaml                # Configurações principais
│       ├── application-docker.yaml         # Configurações Docker
│       ├── data.sql                        # Dados iniciais
│       └── swagger.yaml                    # Especificação OpenAPI
└── test/                                   # Testes unitários
```

## 🔧 Configurações Importantes

### Variáveis de Ambiente
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:h2:mem:inventory_db
SPRING_PROFILES_ACTIVE=docker

# RabbitMQ
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672

# Redis (se disponível)
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379
```

### Profiles
- **default**: Execução local com H2 e RabbitMQ local
- **docker**: Execução em containers com serviços externos

## 🚀 Próximos Passos

- [ ] Implementar endpoint para consulta de mensagens DLQ
- [ ] Adicionar reprocessamento de mensagens falhadas
- [ ] Adicionar autenticação e autorização
- [ ] Implementar testes de integração
- [ ] Adicionar observabilidade

