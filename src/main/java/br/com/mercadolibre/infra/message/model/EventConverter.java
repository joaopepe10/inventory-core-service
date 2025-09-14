package br.com.mercadolibre.infra.message.model;

import br.com.mercadolibre.infra.sql.event.model.EventEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventConverter {

    public static EventEntity toEntity(EventPayload eventPayload) {
        return EventEntity.builder()
                .eventId(eventPayload.eventId())
                .eventType(eventPayload.eventType())
                .changeType(eventPayload.changeType())
                .aggregateId(eventPayload.aggregateId())
                .source(eventPayload.source())
                .createdAt(eventPayload.createdAt())
                .productId(eventPayload.payload().productId())
                .storeId(eventPayload.payload().storeId())
                .quantity(eventPayload.payload().quantity())
                .availableQuantity(eventPayload.payload().availableQuantity())
                .reservedQuantity(eventPayload.payload().reservedQuantity())
                .processed(false)
                .build();
    }

    public static EventPayload toPayload(EventEntity eventEntity) {
        Payload payload = Payload.builder()
                .productId(eventEntity.getProductId())
                .storeId(eventEntity.getStoreId())
                .quantity(eventEntity.getQuantity())
                .availableQuantity(eventEntity.getAvailableQuantity())
                .reservedQuantity(eventEntity.getReservedQuantity())
                .build();

        return EventPayload.builder()
                .eventId(eventEntity.getEventId())
                .eventType(eventEntity.getEventType())
                .changeType(eventEntity.getChangeType())
                .aggregateId(eventEntity.getAggregateId())
                .source(eventEntity.getSource())
                .createdAt(eventEntity.getCreatedAt())
                .payload(payload)
                .build();
    }
}
