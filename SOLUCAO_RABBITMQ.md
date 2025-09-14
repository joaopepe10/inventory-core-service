# Solução para Problemas de Serialização RabbitMQ - Inventory Core Service

## Problemas Identificados

Os erros que você estava enfrentando eram causados por:

1. **Conflito de Serialização**: Mistura entre serialização Java nativa e JSON
2. **Classes Não Autorizadas**: `UpdateInventoryMessage` e `java.util.UUID` não estavam na lista de classes permitidas
3. **Conversores Incompatíveis**: Falta de configuração adequada para conversão de mensagens
4. **Content-Type Incorreto**: Mensagens com `application/x-java-serialized-object` em vez de `application/json`

## Soluções Implementadas

### 1. Entidade JPA para Eventos (`EventEntity`)

**Arquivo**: `src/main/java/br/com/mercadolibre/infra/sql/event/model/EventEntity.java`

- Criada entidade JPA para persistir eventos do RabbitMQ
- Campos desnormalizados do payload para melhor performance
- Controle de processamento com flags `processed` e `processedAt`
- Índices para otimização de consultas

```sql
-- Tabela criada no data.sql
CREATE TABLE IF NOT EXISTS events (
    event_id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    source VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    product_id VARCHAR(255),
    store_id VARCHAR(255),
    quantity INTEGER,
    available_quantity INTEGER,
    reserved_quantity INTEGER,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMP
);
```

### 2. Repositório JPA (`EventRepository`)

**Arquivo**: `src/main/java/br/com/mercadolibre/infra/sql/event/repository/EventRepository.java`

- Métodos de consulta otimizados para diferentes cenários
- Busca por eventos não processados
- Consultas por produto, loja, período, etc.

### 3. Configuração RabbitMQ Corrigida (`RabbitMQConfig`)

**Arquivo**: `src/main/java/br/com/mercadolibre/core/configuration/message/RabbitMQConfig.java`

**Principais correções**:
- Configuração explícita do `Jackson2JsonMessageConverter`
- Factory de container com conversor JSON configurado
- Eliminação de conflitos de serialização

```java
@Bean
@Primary
public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
}

@Bean
public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        MessageConverter jsonMessageConverter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jsonMessageConverter);
    return factory;
}
```

### 4. Subscriber Atualizado (`InventoryUpdateSubscriber`)

**Arquivo**: `src/main/java/br/com/mercadolibre/infra/message/InventoryUpdateSubscriber.java`

**Melhorias implementadas**:
- Recebe diretamente `EventPayload` em vez de `Message<EventPayload>`
- Implementação de idempotência verificando eventos já processados
- Persistência automática de eventos
- Tratamento robusto de erros com logs detalhados
- Uso do conversor para simplificar mapeamento

### 5. Conversor de Utilidade (`EventConverter`)

**Arquivo**: `src/main/java/br/com/mercadolibre/infra/message/model/EventConverter.java`

- Conversão bidirecional entre `EventPayload` e `EventEntity`
- Encapsulamento da lógica de mapeamento
- Reutilização em diferentes partes do código

### 6. Serviço de Domínio (`EventService`)

**Arquivo**: `src/main/java/br/com/mercadolibre/domain/service/EventService.java`

- Lógica de negócio para gerenciamento de eventos
- Processamento de eventos em lote
- Métodos de consulta de alto nível
- Controle transacional

## Como Testar a Solução

### 1. Executar a Aplicação

```bash
mvn spring-boot:run
```

### 2. Enviar Mensagem JSON para o RabbitMQ

```json
{
    "eventId": "evt-12345",
    "eventType": "UPDATED",
    "changeType": "INCREASE",
    "aggregateId": "inventory-abc-123",
    "source": "inventory-service",
    "createdAt": "2024-09-14T15:30:00",
    "payload": {
        "productId": "660e8400-e29b-41d4-a716-446655440006",
        "storeId": "550e8400-e29b-41d4-a716-446655440001",
        "quantity": 10,
        "availableQuantity": 8,
        "reservedQuantity": 2
    }
}
```

### 3. Verificar Logs

A aplicação deve processar a mensagem sem erros e persistir o evento na tabela `events`.

## Benefícios da Solução

1. **Resolve Problemas de Serialização**: Usa JSON consistentemente
2. **Persistência de Eventos**: Todos os eventos são armazenados para auditoria
3. **Idempotência**: Evita processamento duplicado de eventos
4. **Robustez**: Tratamento adequado de erros e logging
5. **Escalabilidade**: Estrutura preparada para crescimento
6. **Maintibilidade**: Código organizado e bem estruturado

## Arquitetura Implementada

```
RabbitMQ (JSON) → InventoryUpdateSubscriber → EventConverter → EventEntity → Database
                                            ↓
                                      EventService (Business Logic)
```

A solução segue os princípios de arquitetura limpa, separando responsabilidades entre infraestrutura (RabbitMQ, JPA) e domínio (regras de negócio).

## Próximos Passos Recomendados

1. Adicionar Dead Letter Queue (DLQ) para mensagens com falha
2. Implementar retry com backoff exponencial
3. Adicionar métricas e monitoramento
4. Implementar testes de integração
5. Adicionar Spring Web se precisar de APIs REST para consultar eventos
