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

            var eventEntity = stockMapper.toEntity(eventPayload);

            eventRepository.save(eventEntity);

            processInventoryUpdate(eventEntity);

            log.info("Evento processado com sucesso: eventId=[{}]", eventPayload.eventId());
            publisher.sendMessage(eventPayload);
        } catch (Exception e) {
            log.error("Erro ao processar evento: eventId=[{}], erro={}",
                    eventPayload.eventId(), e.getMessage(), e);
            throw e;
        }

    }

    public Integer findCurrentStockByProductId(String productId) {
        var entities = eventRepository.findCurrentStockByProductId(productId);
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


    private void processInventoryUpdate(EventStockEntity eventEntity) {
        try {
            eventEntity.setProcessed(true);
            eventEntity.setProcessedAt(LocalDateTime.now());
            eventRepository.save(eventEntity);

        } catch (Exception e) {
            log.error("Erro ao processar atualização de inventário: eventId={}", eventEntity.getEventId(), e);
            throw e;
        }
    }

}
