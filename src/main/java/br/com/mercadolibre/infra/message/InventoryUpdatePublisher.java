package br.com.mercadolibre.infra.message;


import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static br.com.mercadolibre.core.constants.RabbitMQConstants.SEND_EVENT_INVENTORY_QUEUE;


@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryUpdatePublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.publisher.exchange}")
    private String publisherExchange;


    public void sendMessage(@Valid UpdateInventoryMessage message) {
        log.info("Enviando mensagem de atualização de inventário para a fila: {} e de loja {}", SEND_EVENT_INVENTORY_QUEUE, message.messageOrigin());
        rabbitTemplate.convertAndSend(publisherExchange, "", message);
    }
}
