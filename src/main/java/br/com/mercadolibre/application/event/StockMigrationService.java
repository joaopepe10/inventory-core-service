package br.com.mercadolibre.application.event;

import br.com.mercadolibre.infra.message.model.ChangeType;
import br.com.mercadolibre.infra.message.model.EventType;
import br.com.mercadolibre.infra.sql.event.model.EventStockEntity;
import br.com.mercadolibre.infra.sql.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMigrationService {

    private final EventRepository eventRepository;

    @Transactional
    public void migrateStockToEvents() {
        log.info("Iniciando migração de estoque para eventos...");

        List<EventStockEntity> events = createStockEvents();

        int processedCount = 0;
        for (EventStockEntity event : events) {
            if (eventRepository.findByEventId(event.getEventId()).isEmpty()) {
                eventRepository.save(event);
                processedCount++;
                log.debug("Evento criado: {}", event.getEventId());
            } else {
                log.debug("Evento já existe: {}", event.getEventId());
            }
        }

        log.info("Migração concluída! {} eventos criados de {} registros de estoque",
                processedCount, events.size());
    }

    private List<EventStockEntity> createStockEvents() {
        List<EventStockEntity> events = new ArrayList<>();
        String source = "stock-migration-service";
        LocalDateTime now = LocalDateTime.now();

        // TECNOLOGIA
        // iPhone 15 Pro (TECH001) - 3 lojas
        events.add(createEvent("evt-tech001-store001", "660e8400-e29b-41d4-a716-446655440006",
                "550e8400-e29b-41d4-a716-446655440001", 15, 13, 2, source, now));
        events.add(createEvent("evt-tech001-store002", "660e8400-e29b-41d4-a716-446655440006",
                "550e8400-e29b-41d4-a716-446655440002", 12, 11, 1, source, now));
        events.add(createEvent("evt-tech001-store003", "660e8400-e29b-41d4-a716-446655440006",
                "550e8400-e29b-41d4-a716-446655440003", 8, 8, 0, source, now));

        // Samsung Galaxy S24 (TECH002) - 2 lojas
        events.add(createEvent("evt-tech002-store001", "660e8400-e29b-41d4-a716-446655440007",
                "550e8400-e29b-41d4-a716-446655440001", 18, 15, 3, source, now));
        events.add(createEvent("evt-tech002-store002", "660e8400-e29b-41d4-a716-446655440007",
                "550e8400-e29b-41d4-a716-446655440002", 14, 13, 1, source, now));

        // MacBook Air M2 (TECH003) - 2 lojas
        events.add(createEvent("evt-tech003-store001", "660e8400-e29b-41d4-a716-446655440008",
                "550e8400-e29b-41d4-a716-446655440001", 10, 9, 1, source, now));
        events.add(createEvent("evt-tech003-store002", "660e8400-e29b-41d4-a716-446655440008",
                "550e8400-e29b-41d4-a716-446655440002", 8, 8, 0, source, now));

        // PlayStation 5 (TECH004) - 2 lojas
        events.add(createEvent("evt-tech004-store001", "660e8400-e29b-41d4-a716-446655440009",
                "550e8400-e29b-41d4-a716-446655440001", 12, 10, 2, source, now));
        events.add(createEvent("evt-tech004-store002", "660e8400-e29b-41d4-a716-446655440009",
                "550e8400-e29b-41d4-a716-446655440002", 8, 7, 1, source, now));

        // Smart TV Samsung (TECH005) - 2 lojas
        events.add(createEvent("evt-tech005-store001", "660e8400-e29b-41d4-a716-44665544000a",
                "550e8400-e29b-41d4-a716-446655440001", 25, 22, 3, source, now));
        events.add(createEvent("evt-tech005-store002", "660e8400-e29b-41d4-a716-44665544000a",
                "550e8400-e29b-41d4-a716-446655440002", 20, 18, 2, source, now));

        // SUPERMERCADO
        // Arroz Tio João (SUPER001) - 3 lojas
        events.add(createEvent("evt-super001-store001", "660e8400-e29b-41d4-a716-446655440003",
                "550e8400-e29b-41d4-a716-446655440001", 200, 180, 20, source, now));
        events.add(createEvent("evt-super001-store002", "660e8400-e29b-41d4-a716-446655440003",
                "550e8400-e29b-41d4-a716-446655440002", 150, 135, 15, source, now));
        events.add(createEvent("evt-super001-store003", "660e8400-e29b-41d4-a716-446655440003",
                "550e8400-e29b-41d4-a716-446655440003", 180, 170, 10, source, now));

        // Óleo de Soja (SUPER002) - 2 lojas
        events.add(createEvent("evt-super002-store001", "660e8400-e29b-41d4-a716-446655440004",
                "550e8400-e29b-41d4-a716-446655440001", 120, 110, 10, source, now));
        events.add(createEvent("evt-super002-store002", "660e8400-e29b-41d4-a716-446655440004",
                "550e8400-e29b-41d4-a716-446655440002", 100, 92, 8, source, now));

        // CASA E MÓVEIS
        // Sofá Retrátil (HOME001) - 2 lojas
        events.add(createEvent("evt-home001-store001", "660e8400-e29b-41d4-a716-44665544000b",
                "550e8400-e29b-41d4-a716-446655440001", 5, 4, 1, source, now));
        events.add(createEvent("evt-home001-store003", "660e8400-e29b-41d4-a716-44665544000b",
                "550e8400-e29b-41d4-a716-446655440003", 3, 3, 0, source, now));

        // Mesa de Jantar (HOME002) - 2 lojas
        events.add(createEvent("evt-home002-store001", "660e8400-e29b-41d4-a716-44665544000c",
                "550e8400-e29b-41d4-a716-446655440001", 8, 7, 1, source, now));
        events.add(createEvent("evt-home002-store002", "660e8400-e29b-41d4-a716-44665544000c",
                "550e8400-e29b-41d4-a716-446655440002", 6, 6, 0, source, now));

        // ELETRODOMÉSTICOS
        // Geladeira Brastemp (ELETRO001) - 2 lojas
        events.add(createEvent("evt-eletro001-store001", "660e8400-e29b-41d4-a716-44665544000e",
                "550e8400-e29b-41d4-a716-446655440001", 15, 13, 2, source, now));
        events.add(createEvent("evt-eletro001-store002", "660e8400-e29b-41d4-a716-44665544000e",
                "550e8400-e29b-41d4-a716-446655440002", 12, 11, 1, source, now));

        // Air Fryer (ELETRO003) - 2 lojas
        events.add(createEvent("evt-eletro003-store001", "660e8400-e29b-41d4-a716-446655440010",
                "550e8400-e29b-41d4-a716-446655440001", 45, 40, 5, source, now));
        events.add(createEvent("evt-eletro003-store002", "660e8400-e29b-41d4-a716-446655440010",
                "550e8400-e29b-41d4-a716-446655440002", 35, 32, 3, source, now));

        // MODA
        // Tênis Nike (FASHION002) - 2 lojas
        events.add(createEvent("evt-fashion002-store001", "660e8400-e29b-41d4-a716-446655440023",
                "550e8400-e29b-41d4-a716-446655440001", 60, 52, 8, source, now));
        events.add(createEvent("evt-fashion002-store002", "660e8400-e29b-41d4-a716-446655440023",
                "550e8400-e29b-41d4-a716-446655440002", 45, 40, 5, source, now));

        // Camiseta Polo Lacoste (FASHION001) - 2 lojas
        events.add(createEvent("evt-fashion001-store001", "660e8400-e29b-41d4-a716-446655440022",
                "550e8400-e29b-41d4-a716-446655440001", 30, 27, 3, source, now));
        events.add(createEvent("evt-fashion001-store003", "660e8400-e29b-41d4-a716-446655440022",
                "550e8400-e29b-41d4-a716-446655440003", 25, 23, 2, source, now));

        // PET SHOP
        // Ração Golden (PET001) - 2 lojas
        events.add(createEvent("evt-pet001-store001", "660e8400-e29b-41d4-a716-44665544001a",
                "550e8400-e29b-41d4-a716-446655440001", 80, 72, 8, source, now));
        events.add(createEvent("evt-pet001-store002", "660e8400-e29b-41d4-a716-44665544001a",
                "550e8400-e29b-41d4-a716-446655440002", 65, 60, 5, source, now));

        // MAIS VENDIDOS
        // Carregador iPhone (BEST001) - 3 lojas
        events.add(createEvent("evt-best001-store001", "660e8400-e29b-41d4-a716-44665544002a",
                "550e8400-e29b-41d4-a716-446655440001", 150, 135, 15, source, now));
        events.add(createEvent("evt-best001-store002", "660e8400-e29b-41d4-a716-44665544002a",
                "550e8400-e29b-41d4-a716-446655440002", 120, 108, 12, source, now));
        events.add(createEvent("evt-best001-store003", "660e8400-e29b-41d4-a716-44665544002a",
                "550e8400-e29b-41d4-a716-446655440003", 100, 90, 10, source, now));

        // Película iPhone (BEST002) - 2 lojas
        events.add(createEvent("evt-best002-store001", "660e8400-e29b-41d4-a716-44665544002b",
                "550e8400-e29b-41d4-a716-446655440001", 200, 180, 20, source, now));
        events.add(createEvent("evt-best002-store002", "660e8400-e29b-41d4-a716-44665544002b",
                "550e8400-e29b-41d4-a716-446655440002", 180, 162, 18, source, now));

        // PRODUTOS COM ESTOQUE ZERADO (usando DECREASE para indicar que não há estoque)
        events.add(createZeroStockEvent("evt-vehicle002-store001", "660e8400-e29b-41d4-a716-446655440002",
                "550e8400-e29b-41d4-a716-446655440001", source, now));
        events.add(createZeroStockEvent("evt-fashion003-store002", "660e8400-e29b-41d4-a716-446655440024",
                "550e8400-e29b-41d4-a716-446655440002", source, now));
        events.add(createZeroStockEvent("evt-tool001-store003", "660e8400-e29b-41d4-a716-446655440014",
                "550e8400-e29b-41d4-a716-446655440003", source, now));

        return events;
    }

    private EventStockEntity createEvent(String eventIdBase, String productId, String storeId,
                                         int quantity, int availableQuantity, int reservedQuantity,
                                         String source, LocalDateTime createdAt) {
        String eventId = eventIdBase + "-" + System.currentTimeMillis();
        String aggregateId = "inventory-" + extractProductCode(productId) + "-" + extractStoreCode(storeId);

        return EventStockEntity.builder()
                .eventId(eventId)
                .eventType(EventType.CREATED)
                .changeType(ChangeType.INCREASE)
                .aggregateId(aggregateId)
                .source(source)
                .createdAt(createdAt)
                .productId(productId)
                .storeId(storeId)
                .quantity(quantity)
                .availableQuantity(availableQuantity)
                .reservedQuantity(reservedQuantity)
                .processed(true)
                .processedAt(createdAt)
                .build();
    }

    private EventStockEntity createZeroStockEvent(String eventIdBase, String productId, String storeId,
                                                  String source, LocalDateTime createdAt) {
        String eventId = eventIdBase + "-" + System.currentTimeMillis();
        String aggregateId = "inventory-" + extractProductCode(productId) + "-" + extractStoreCode(storeId);

        return EventStockEntity.builder()
                .eventId(eventId)
                .eventType(EventType.CREATED)
                .changeType(ChangeType.DECREASE)
                .aggregateId(aggregateId)
                .source(source)
                .createdAt(createdAt)
                .productId(productId)
                .storeId(storeId)
                .quantity(0)
                .availableQuantity(0)
                .reservedQuantity(0)
                .processed(true)
                .processedAt(createdAt)
                .build();
    }

    private String extractProductCode(String productId) {
        // Extrai os últimos 4 caracteres do UUID para criar um código mais legível
        return productId.substring(productId.length() - 4);
    }

    private String extractStoreCode(String storeId) {
        // Extrai os últimos 4 caracteres do UUID para criar um código mais legível
        return storeId.substring(storeId.length() - 4);
    }
}
