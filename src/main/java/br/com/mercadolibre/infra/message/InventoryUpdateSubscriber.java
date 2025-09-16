package br.com.mercadolibre.infra.message;

import br.com.mercadolibre.domain.event.EventStockService;
import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.random.RandomGenerator;

import static br.com.mercadolibre.core.constants.RabbitMQConstants.PROCESS_UPDATE_INVENTORY_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryUpdateSubscriber {

    private final EventStockService eventStockService;

    @Value("${queue.simulate.failure:false}")
    private boolean simulateFailure;

    @RabbitListener(queues = PROCESS_UPDATE_INVENTORY_QUEUE)
    public void receiveMessage(@Valid Message<UpdateInventoryMessage> message) {
        var randomNumber = Random.from(RandomGenerator.getDefault()).nextInt(1, 3);

        if (simulateFailure) {
            log.error("Simulating processing failure for message: {}", message);
            throw new RuntimeException("Simulated processing failure");
        }

        var eventPayload = message.getPayload();
        eventStockService.processEvent(eventPayload);
    }

}
