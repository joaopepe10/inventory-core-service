# Inventory Core Service

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-green.svg)](https://swagger.io/specification/)

Sistema de gerenciamento de eventos de inventário com arquitetura orientada a eventos, integração RabbitMQ e API REST completa.

## 📋 Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [API Documentation](#api-documentation)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Funcionalidades](#funcionalidades)
- [Exemplos de Uso](#exemplos-de-uso)
- [Banco de Dados](#banco-de-dados)
- [Mensageria](#mensageria)
- [Contribuindo](#contribuindo)

## 🎯 Sobre o Projeto

O **Inventory Core Service** é um microserviço responsável por gerenciar eventos de inventário em tempo real. O sistema processa mudanças de estoque, produtos e lojas através de uma arquitetura orientada a eventos, garantindo rastreabilidade completa e processamento assíncrono.

### Principais Características

- ✅ **Event Sourcing**: Todos os eventos são persistidos para auditoria completa
- ✅ **Processamento Assíncrono**: Integração com RabbitMQ para mensageria
- ✅ **API REST**: Endpoints documentados com OpenAPI 3.0
- ✅ **Idempotência**: Prevenção de processamento duplicado
- ✅ **Validações Robustas**: Validação de dados de negócio e entrada
- ✅ **Monitoramento**: Health checks e métricas integradas

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   RabbitMQ      │    │   REST API      │    │   H2 Database   │
│   (Events)      │────│   (Commands)    │────│   (Events)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         ▼                        ▼                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                Inventory Core Service                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │ Subscriber  │  │ Controller  │  │ Repository  │           │
│  │ (Consumer)  │  │ (REST API)  │  │ (JPA)       │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
│         │                 │                 │                 │
│         └─────────────────┼─────────────────┘                 │
│                           │                                   │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │             Event Processing Engine                     │ │
│  │  • Validation • Persistence • Business Logic           │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 🛠️ Tecnologias Utilizadas

### Core Framework
- **Java 21**: Linguagem de programação
- **Spring Boot 3.5.5**: Framework principal
- **Spring Web**: APIs REST
- **Spring Data JPA**: Persistência de dados
- **Spring AMQP**: Integração RabbitMQ

### Documentação e Validação
- **OpenAPI 3.0**: Especificação da API
- **SpringDoc**: Geração automática de documentação
- **Bean Validation**: Validação de dados

### Ferramentas de Desenvolvimento
- **MapStruct**: Mapeamento de objetos
- **Lombok**: Redução de boilerplate
- **Maven**: Gerenciamento de dependências

### Infraestrutura
- **H2 Database**: Banco em memória para desenvolvimento
- **RabbitMQ**: Message broker
- **Docker**: Containerização

## 📦 Pré-requisitos

- **Java 21+**
- **Maven 3.8+**
- **RabbitMQ** (Docker recomendado)
- **Git**

### Iniciando RabbitMQ com Docker

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

## 🚀 Instalação e Execução

### 1. Clone o Repositório

```bash
git clone <repository-url>
cd inventory-core-service
```

### 2. Compile o Projeto

```bash
mvn clean compile
```

### 3. Execute a Aplicação

```bash
mvn spring-boot:run
```

### 4. Verifique se está funcionando

A aplicação estará disponível em: `http://localhost:8081`

- **Health Check**: `http://localhost:8081/actuator/health`
- **API Documentation**: `http://localhost:8081/swagger-ui.html`
- **H2 Console**: `http://localhost:8081/h2-console`

## 📚 API Documentation

### Swagger UI
Acesse `http://localhost:8081/swagger-ui.html` para documentação interativa da API.

### Endpoint Principal

#### Criar Evento
```http
POST /api/events
Content-Type: application/json

{
  "eventType": "CREATED",
  "changeType": "INCREASE",
  "aggregateId": "inventory-tech001-store001",
  "source": "api-request",
  "productId": "660e8400-e29b-41d4-a716-446655440006",
  "storeId": "550e8400-e29b-41d4-a716-446655440001",
  "quantity": 10,
  "availableQuantity": 8,
  "reservedQuantity": 2
}
```

#### Resposta de Sucesso (201)
```json
{
  "eventId": "evt-api-request-1726329600000-abc12345",
  "eventType": "CREATED",
  "changeType": "INCREASE",
  "aggregateId": "inventory-tech001-store001",
  "source": "api-request",
  "productId": "660e8400-e29b-41d4-a716-446655440006",
  "storeId": "550e8400-e29b-41d4-a716-446655440001",
  "quantity": 10,
  "availableQuantity": 8,
  "reservedQuantity": 2,
  "processed": false,
  "createdAt": "2024-09-14T15:30:00Z",
  "processedAt": null
}
```

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/mercadolibre/
│   │   ├── api/                    # Classes geradas pelo OpenAPI
│   │   │   ├── controller/         # Interfaces da API
│   │   │   └── model/              # DTOs da API
│   │   ├── core/
│   │   │   └── configuration/      # Configurações do sistema
│   │   ├── domain/
│   │   │   └── service/            # Serviços de domínio
│   │   └── infra/
│   │       ├── message/            # Integração RabbitMQ
│   │       ├── rest/               # Controllers e mappers REST
│   │       └── sql/                # Entidades e repositórios JPA
│   └── resources/
│       ├── application.yaml        # Configurações da aplicação
│       ├── data.sql               # Scripts de inicialização
│       └── swagger.yaml           # Especificação OpenAPI
└── test/                          # Testes unitários e integração
```

## ⚡ Funcionalidades

### 🎯 Processamento de Eventos

- **Criação de Eventos**: Via API REST ou mensagem RabbitMQ
- **Validação Automática**: Campos obrigatórios e regras de negócio
- **Persistência**: Armazenamento em banco para auditoria
- **Idempotência**: Prevenção de duplicatas por `aggregateId`

### 📨 Integração RabbitMQ

- **Consumer**: Processa mensagens da fila `PROCESS_UPDATE_INVENTORY_QUEUE`
- **Serialização JSON**: Conversão automática de mensagens
- **Error Handling**: Tratamento robusto de erros com retry

### 🗃️ Gestão de Dados

- **Event Sourcing**: Histórico completo de mudanças
- **Queries Otimizadas**: Consultas por produto, loja, período
- **Migração de Dados**: Scripts para importar estoque existente

## 🔧 Exemplos de Uso

### Criar Evento via cURL

```bash
curl -X POST http://localhost:8081/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "UPDATED",
    "changeType": "DECREASE",
    "aggregateId": "inventory-tech002-store002",
    "source": "external-system",
    "productId": "660e8400-e29b-41d4-a716-446655440007",
    "storeId": "550e8400-e29b-41d4-a716-446655440002",
    "quantity": 5,
    "availableQuantity": 3,
    "reservedQuantity": 2
  }'
```

### Enviar Mensagem RabbitMQ

```java
// Exemplo usando EventPayload
EventPayload event = EventPayload.builder()
    .eventId(UUID.randomUUID().toString())
    .eventType(EventType.CREATED)
    .changeType(ChangeType.INCREASE)
    .aggregateId("inventory-example")
    .source("test-system")
    .createdAt(LocalDateTime.now())
    .payload(Payload.builder()
        .productId("product-123")
        .storeId("store-456")
        .quantity(15)
        .availableQuantity(12)
        .reservedQuantity(3)
        .build())
    .build();

rabbitTemplate.convertAndSend("PROCESS_UPDATE_INVENTORY_QUEUE", event);
```

## 🗄️ Banco de Dados

### Configuração H2

- **URL**: `jdbc:h2:mem:testdb`
- **Console**: `http://localhost:8081/h2-console`
- **Usuário**: `sa`
- **Senha**: *(vazio)*

### Schema de Eventos

```sql
CREATE TABLE events (
    event_id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    source VARCHAR(50) NOT NULL,
    product_id VARCHAR(255),
    store_id VARCHAR(255),
    quantity INTEGER,
    available_quantity INTEGER,
    reserved_quantity INTEGER,
    processed BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);
```

### Dados de Exemplo

O sistema inclui dados de exemplo com:
- **3 lojas**: São Paulo, Rio de Janeiro, Belo Horizonte
- **40+ produtos**: Tecnologia, Supermercado, Casa & Móveis, etc.
- **37 eventos de estoque**: Estados iniciais de inventário

## 📡 Mensageria

### Configuração RabbitMQ

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### Filas

- **PROCESS_UPDATE_INVENTORY_QUEUE**: Processa atualizações de inventário

### Formato de Mensagem

```json
{
  "eventId": "evt-unique-id",
  "eventType": "CREATED",
  "changeType": "INCREASE",
  "aggregateId": "inventory-product-store",
  "source": "source-system",
  "createdAt": "2024-09-14T15:30:00",
  "payload": {
    "productId": "product-uuid",
    "storeId": "store-uuid",
    "quantity": 10,
    "availableQuantity": 8,
    "reservedQuantity": 2
  }
}
```

## 🐳 Docker

### Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Executar com Docker
```bash
# Build
mvn clean package
docker build -t inventory-core-service .

# Run
docker run -p 8081:8081 inventory-core-service
```

## 🧪 Testes

### Executar Testes
```bash
mvn test
```

### Teste de Integração
```bash
mvn integration-test
```

## 📊 Monitoramento

### Health Checks
- **Aplicação**: `GET /actuator/health`
- **Banco de Dados**: Verificação automática de conectividade
- **RabbitMQ**: Status das conexões

### Métricas
- **Endpoint**: `/actuator/metrics`
- **Prometheus**: Métricas exportadas automaticamente

## 🤝 Contribuindo

1. **Fork** o projeto
2. Crie sua **feature branch** (`git checkout -b feature/nova-funcionalidade`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. **Push** para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um **Pull Request**

### Padrões de Código

- Usar **Lombok** para reduzir boilerplate
- Documentar APIs com **OpenAPI**
- Implementar testes unitários
- Seguir convenções **Spring Boot**

## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

## 📞 Suporte

Para dúvidas ou suporte:

- 📧 **Email**: inventory@mercadolibre.com
- 📝 **Issues**: Use o sistema de issues do GitHub
- 📖 **Wiki**: Documentação adicional na wiki do projeto

---

⭐ **Gostou do projeto? Deixe uma estrela!**
