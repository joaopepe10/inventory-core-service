package br.com.mercadolibre.infra.message;

import br.com.mercadolibre.domain.event.EventStockService;
import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static br.com.mercadolibre.core.constants.RabbitMQConstants.PROCESS_UPDATE_INVENTORY_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryUpdateSubscriber {

    private final EventStockService eventStockService;

    @RabbitListener(queues = PROCESS_UPDATE_INVENTORY_QUEUE)
    public void receiveMessage(@Valid Message<UpdateInventoryMessage> message) {
        var eventPayload = message.getPayload();
        eventStockService.processEvent(eventPayload);
    }

}
