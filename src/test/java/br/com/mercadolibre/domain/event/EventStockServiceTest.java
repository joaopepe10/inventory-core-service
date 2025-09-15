package br.com.mercadolibre.domain.event;

import br.com.mercadolibre.domain.event.mapper.EventStockMapper;
import br.com.mercadolibre.infra.message.InventoryUpdatePublisher;
import br.com.mercadolibre.infra.message.model.ChangeType;
import br.com.mercadolibre.infra.message.model.EventType;
import br.com.mercadolibre.infra.message.model.Payload;
import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import br.com.mercadolibre.infra.sql.event.model.EventStockEntity;
import br.com.mercadolibre.infra.sql.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventStockService Tests")
class EventStockServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private InventoryUpdatePublisher publisher;

    @Mock
    private EventStockMapper stockMapper;

    @InjectMocks
    private EventStockService eventStockService;

    private UpdateInventoryMessage updateInventoryMessage;
    private EventStockEntity eventStockEntity;

    @BeforeEach
    void setUp() {
        var payload = Payload.builder()
                .productId("PROD123")
                .storeId("STORE456")
                .quantity(10)
                .build();

        updateInventoryMessage = UpdateInventoryMessage.builder()
                .eventId("EVENT123")
                .eventType(EventType.UPDATED)
                .changeType(ChangeType.INCREASE)
                .aggregateId("AGG123")
                .source("TEST_SOURCE")
                .createdAt(LocalDateTime.now())
                .payload(payload)
                .build();

        eventStockEntity = EventStockEntity.builder()
                .eventId("EVENT123")
                .eventType(EventType.UPDATED)
                .changeType(ChangeType.INCREASE)
                .aggregateId("AGG123")
                .source("TEST_SOURCE")
                .productId("PROD123")
                .storeId("STORE456")
                .quantity(10)
                .processed(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("processEvent Tests")
    class ProcessEventTests {

        @Test
        @DisplayName("Deve processar evento com sucesso quando evento ainda não foi processado")
        void shouldProcessEventSuccessfullyWhenEventNotProcessedBefore() {
            when(eventRepository.findByEventId(updateInventoryMessage.eventId()))
                    .thenReturn(Optional.empty());
            when(stockMapper.toEntity(updateInventoryMessage))
                    .thenReturn(eventStockEntity);
            when(eventRepository.save(any(EventStockEntity.class)))
                    .thenReturn(eventStockEntity);

            eventStockService.processEvent(updateInventoryMessage);

            verify(eventRepository).findByEventId(updateInventoryMessage.eventId());
            verify(stockMapper).toEntity(updateInventoryMessage);
            verify(eventRepository, times(2)).save(any(EventStockEntity.class));
            verify(publisher).sendMessage(updateInventoryMessage);

            var entityCaptor = ArgumentCaptor.forClass(EventStockEntity.class);
            verify(eventRepository, times(2)).save(entityCaptor.capture());

            var savedEntities = entityCaptor.getAllValues();

            assertThat(savedEntities.get(0)).isNotNull();

            var processedEntity = savedEntities.get(1);
            assertThat(processedEntity.getProcessed()).isTrue();
            assertThat(processedEntity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve ignorar processamento quando evento já foi processado anteriormente")
        void shouldIgnoreProcessingWhenEventAlreadyProcessed() {
            when(eventRepository.findByEventId(updateInventoryMessage.eventId()))
                    .thenReturn(Optional.of(eventStockEntity));

            eventStockService.processEvent(updateInventoryMessage);

            verify(eventRepository).findByEventId(updateInventoryMessage.eventId());
            verify(stockMapper, never()).toEntity(any());
            verify(eventRepository, never()).save(any());
            verify(publisher, never()).sendMessage(any());
        }

        @Test
        @DisplayName("Deve propagar exceção quando erro ocorre durante processamento")
        void shouldPropagateExceptionWhenErrorOccursDuringProcessing() {
            when(eventRepository.findByEventId(updateInventoryMessage.eventId()))
                    .thenReturn(Optional.empty());
            when(stockMapper.toEntity(updateInventoryMessage))
                    .thenReturn(eventStockEntity);
            when(eventRepository.save(any(EventStockEntity.class)))
                    .thenThrow(new RuntimeException("Database error"));

            assertThatThrownBy(() -> eventStockService.processEvent(updateInventoryMessage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");

            verify(eventRepository).findByEventId(updateInventoryMessage.eventId());
            verify(stockMapper).toEntity(updateInventoryMessage);
            verify(eventRepository).save(any(EventStockEntity.class));
            verify(publisher, never()).sendMessage(any());
        }

        @Test
        @DisplayName("Deve propagar exceção quando mapper falha")
        void shouldPropagateExceptionWhenMapperFails() {
            when(eventRepository.findByEventId(updateInventoryMessage.eventId()))
                    .thenReturn(Optional.empty());
            when(stockMapper.toEntity(updateInventoryMessage))
                    .thenThrow(new RuntimeException("Mapping error"));

            assertThatThrownBy(() -> eventStockService.processEvent(updateInventoryMessage))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Mapping error");

            verify(eventRepository).findByEventId(updateInventoryMessage.eventId());
            verify(stockMapper).toEntity(updateInventoryMessage);
            verify(eventRepository, never()).save(any());
            verify(publisher, never()).sendMessage(any());
        }
    }

    @Nested
    @DisplayName("findCurrentStockByProductId Tests")
    class FindCurrentStockByProductIdTests {

        @Test
        @DisplayName("Deve retornar 0 quando não há eventos para o produto")
        void shouldReturnZeroWhenNoEventsForProduct() {
            var productId = "PROD123";
            when(eventRepository.findCurrentStockByProductId(productId))
                    .thenReturn(Collections.emptyList());

            var result = eventStockService.findCurrentStockByProductId(productId);

            assertThat(result).isEqualTo(0);
            verify(eventRepository).findCurrentStockByProductId(productId);
        }

        @Test
        @DisplayName("Deve calcular estoque corretamente com apenas eventos de aumento")
        void shouldCalculateStockCorrectlyWithOnlyIncreaseEvents() {
            var productId = "PROD123";
            var events = List.of(
                    createEventEntity("EVENT1", ChangeType.INCREASE, 10),
                    createEventEntity("EVENT2", ChangeType.INCREASE, 5),
                    createEventEntity("EVENT3", ChangeType.INCREASE, 15)
            );

            when(eventRepository.findCurrentStockByProductId(productId))
                    .thenReturn(events);

            var result = eventStockService.findCurrentStockByProductId(productId);

            assertThat(result).isEqualTo(30);
            verify(eventRepository).findCurrentStockByProductId(productId);
        }

        @Test
        @DisplayName("Deve calcular estoque corretamente com apenas eventos de diminuição")
        void shouldCalculateStockCorrectlyWithOnlyDecreaseEvents() {
            var productId = "PROD123";
            var events = List.of(
                    createEventEntity("EVENT1", ChangeType.DECREASE, 10),
                    createEventEntity("EVENT2", ChangeType.DECREASE, 5)
            );

            when(eventRepository.findCurrentStockByProductId(productId))
                    .thenReturn(events);

            var result = eventStockService.findCurrentStockByProductId(productId);

            assertThat(result).isEqualTo(-15);
            verify(eventRepository).findCurrentStockByProductId(productId);
        }

        @Test
        @DisplayName("Deve calcular estoque corretamente com eventos mistos de aumento e diminuição")
        void shouldCalculateStockCorrectlyWithMixedEvents() {
            var productId = "PROD123";
            var events = List.of(
                    createEventEntity("EVENT1", ChangeType.INCREASE, 100),
                    createEventEntity("EVENT2", ChangeType.DECREASE, 20),
                    createEventEntity("EVENT3", ChangeType.INCREASE, 50),
                    createEventEntity("EVENT4", ChangeType.DECREASE, 10),
                    createEventEntity("EVENT5", ChangeType.INCREASE, 5)
            );

            when(eventRepository.findCurrentStockByProductId(productId))
                    .thenReturn(events);

            var result = eventStockService.findCurrentStockByProductId(productId);

            assertThat(result).isEqualTo(125);
            verify(eventRepository).findCurrentStockByProductId(productId);
        }

        @Test
        @DisplayName("Deve retornar estoque negativo quando há mais diminuições que aumentos")
        void shouldReturnNegativeStockWhenMoreDecreaseThanIncrease() {
            var productId = "PROD123";
            var events = List.of(
                    createEventEntity("EVENT1", ChangeType.INCREASE, 10),
                    createEventEntity("EVENT2", ChangeType.DECREASE, 20)
            );

            when(eventRepository.findCurrentStockByProductId(productId))
                    .thenReturn(events);

            var result = eventStockService.findCurrentStockByProductId(productId);

            assertThat(result).isEqualTo(-10);
            verify(eventRepository).findCurrentStockByProductId(productId);
        }

        @Test
        @DisplayName("Deve lidar com quantidade zero em eventos")
        void shouldHandleZeroQuantityInEvents() {
            var productId = "PROD123";
            var events = List.of(
                    createEventEntity("EVENT1", ChangeType.INCREASE, 0),
                    createEventEntity("EVENT2", ChangeType.DECREASE, 0),
                    createEventEntity("EVENT3", ChangeType.INCREASE, 10)
            );

            when(eventRepository.findCurrentStockByProductId(productId))
                    .thenReturn(events);

            var result = eventStockService.findCurrentStockByProductId(productId);

            assertThat(result).isEqualTo(10);
            verify(eventRepository).findCurrentStockByProductId(productId);
        }

        @Test
        @DisplayName("Deve verificar se método é chamado com productId correto")
        void shouldVerifyMethodIsCalledWithCorrectProductId() {
            var productId = "SPECIFIC_PRODUCT_ID";
            when(eventRepository.findCurrentStockByProductId(productId))
                    .thenReturn(Collections.emptyList());

            eventStockService.findCurrentStockByProductId(productId);

            verify(eventRepository).findCurrentStockByProductId(eq(productId));
        }
    }

    private EventStockEntity createEventEntity(String eventId, ChangeType changeType, Integer quantity) {
        return EventStockEntity.builder()
                .eventId(eventId)
                .eventType(EventType.UPDATED)
                .changeType(changeType)
                .aggregateId("AGG123")
                .source("TEST_SOURCE")
                .productId("PROD123")
                .storeId("STORE456")
                .quantity(quantity)
                .processed(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
