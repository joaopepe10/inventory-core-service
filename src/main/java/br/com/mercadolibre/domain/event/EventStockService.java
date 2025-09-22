package br.com.mercadolibre.domain.event;

import br.com.mercadolibre.domain.event.mapper.EventStockMapper;
import br.com.mercadolibre.infra.message.InventoryUpdatePublisher;
import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import br.com.mercadolibre.infra.sql.event.model.EventStockEntity;
import br.com.mercadolibre.infra.sql.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.mercadolibre.infra.message.model.ChangeType.DECREASE;
import static br.com.mercadolibre.infra.message.model.ChangeType.INCREASE;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventStockService {

    private final EventRepository eventRepository;
    private final InventoryUpdatePublisher publisher;
    private final EventStockMapper stockMapper;

    @Transactional
    public void processEvent(UpdateInventoryMessage eventPayload) {
        try {
            if (eventRepository.findByEventId(eventPayload.eventId()).isPresent()) {
                log.warn("Evento já foi processado anteriormente: eventId={}", eventPayload.eventId());
                return;
            }

            if (eventPayload.changeType() == DECREASE && !isAvailableToPurchase(eventPayload)) {
                log.warn("Estoque insuficiente para processar o evento: eventId={}", eventPayload.eventId());
                // TODO IMPLEMENTAR LOGICA PARA TRATAR EVENTO DE ESTOQUE INSUFICIENTE E REPROCESSAR
                saveEvent(eventPayload);
                return;
            }

            var eventEntity = saveEvent(eventPayload);

            log.info("Evento processado com sucesso: eventId=[{}]", eventPayload.eventId());
            publisher.sendMessage(eventPayload);

            processInventoryUpdate(eventEntity);
        } catch (Exception e) {
            log.error("Erro ao processar evento: eventId=[{}], erro={}",
                    eventPayload.eventId(), e.getMessage(), e);
            throw e;
        }

    }

    private EventStockEntity saveEvent(UpdateInventoryMessage eventPayload) {
        var eventEntity = stockMapper.toEntity(eventPayload);
        return eventRepository.save(eventEntity);
    }

    private boolean isAvailableToPurchase(UpdateInventoryMessage eventPayload) {
        var sum = eventRepository.findCurrentStockByProductIdAndStoreId(eventPayload.payload().productId(), eventPayload.payload().storeId())
                .stream()
                .mapToInt(e -> {
                    if (e.getChangeType() == INCREASE) return e.getQuantity();
                    if (e.getChangeType() == DECREASE) return -e.getQuantity();
                    return 0;
                })
                .sum();

        return eventPayload.payload().quantity() <= sum;
    }

    public Integer findCurrentStockByProductIdAndStoreId(String productId, String storeId) {
        var entities = eventRepository.findCurrentStockByProductIdAndStoreId(productId, storeId);
        return makeSum(entities);
    }

    private Integer makeSum(List<EventStockEntity> entities) {
        if (entities.isEmpty()) {
            return 0;
        }

        return entities.stream()
                .mapToInt(e -> {
                    if (e.getChangeType() == INCREASE) return e.getQuantity();
                    if (e.getChangeType() == DECREASE) return -e.getQuantity();
                    return 0;
                })
                .sum();
    }

    public Integer findCurrentStockByProductId(String productId) {
        var entities = eventRepository.findCurrentStockByProductId(productId);
        return makeSum(entities);
    }

    private void processInventoryUpdate(EventStockEntity eventEntity) {
        try {
            eventEntity.markAsProcessed();
            eventRepository.save(eventEntity);
        } catch (Exception e) {
            log.error("Erro ao processar atualização de inventário: eventId={}", eventEntity.getEventId(), e);
            throw e;
        }
    }

}
