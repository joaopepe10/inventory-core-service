package br.com.mercadolibre.infra.message;


import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryUpdatePublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.publisher.exchange}")
    private String publisherExchange;

    public void sendMessage(@Valid UpdateInventoryMessage message) {
        log.info("Enviando mensagem de atualização de inventário para a exchange: {} e de loja {}", publisherExchange, message.messageOrigin());
        rabbitTemplate.convertAndSend(publisherExchange, "", message);
    }
}
