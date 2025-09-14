package br.com.mercadolibre.infra.message;

import br.com.mercadolibre.infra.message.model.EventConverter;
import br.com.mercadolibre.infra.message.model.EventPayload;
import br.com.mercadolibre.infra.sql.event.model.EventEntity;
import br.com.mercadolibre.infra.sql.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static br.com.mercadolibre.core.configuration.message.RabbitMQConfig.PROCESS_UPDATE_INVENTORY_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryUpdateSubscriber {

    private final EventRepository eventRepository;

    @RabbitListener(queues = PROCESS_UPDATE_INVENTORY_QUEUE)
    @Transactional
    public void receiveMessage(EventPayload eventPayload) {
        try {
            log.info("Recebida mensagem de atualização de inventário: eventId={}, aggregateId={}",
                    eventPayload.eventId(), eventPayload.aggregateId());

            if (eventRepository.findByEventId(eventPayload.eventId()).isPresent()) {
                log.warn("Evento já foi processado anteriormente: eventId={}", eventPayload.eventId());
                return;
            }

            var eventEntity = EventConverter.toEntity(eventPayload);

            eventRepository.save(eventEntity);

            processInventoryUpdate(eventEntity);

            log.info("Evento processado com sucesso: eventId={}", eventPayload.eventId());

        } catch (Exception e) {
            log.error("Erro ao processar mensagem de inventário: eventId={}, erro={}",
                    eventPayload.eventId(), e.getMessage(), e);
            throw e; // TODO VALIDAR RETRY E REPROCESSAMENTO
        }
    }

    private void processInventoryUpdate(EventEntity eventEntity) {
        try {
            log.info("Processando atualização de inventário: productId={}, storeId={}, quantidade={}",
                    eventEntity.getProductId(), eventEntity.getStoreId(), eventEntity.getQuantity());

            eventEntity.setProcessed(true);
            eventEntity.setProcessedAt(LocalDateTime.now());
            eventRepository.save(eventEntity);

        } catch (Exception e) {
            log.error("Erro ao processar atualização de inventário: eventId={}", eventEntity.getEventId(), e);
            throw e;
        }
    }
}
