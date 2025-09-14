package br.com.mercadolibre.infra.message;

import br.com.mercadolibre.infra.message.model.UpdateInventoryMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryUpdateSubscriber {

    @RabbitListener(queues = "PROCESS_UPDATE_INVENTORY_QUEUE")
    public void receiveMessage(Message<UpdateInventoryMessage> message) {
        System.out.println("Received message: " + message);
    }
}
