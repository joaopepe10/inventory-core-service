package br.com.mercadolibre.infra.message;


import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static br.com.mercadolibre.core.constants.RabbitMQConstants.SEND_EVENT_INVENTORY_QUEUE;


@Component
@RequiredArgsConstructor
public class InventoryUpdatePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(@Valid UpdateInventoryMessage message) {
        rabbitTemplate.convertAndSend(SEND_EVENT_INVENTORY_QUEUE, message);
    }
}
