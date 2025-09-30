package br.com.mercadolibre.core.configuration.message;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static br.com.mercadolibre.core.constants.RabbitMQConstants.SEND_EVENT_INVENTORY_QUEUE;
import static br.com.mercadolibre.core.constants.RabbitMQConstants.SEND_EVENT_INVENTORY_QUEUE_DLQ;

@Configuration
@EnableRabbit
public class RabbitMQPublisherConfig {

    @Value("${queue.publisher.exchange}")
    private String publisherExchange;

    @Bean
    @Qualifier("publisherQueue")
    public Queue publisherQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "");
        args.put("x-dead-letter-routing-key", SEND_EVENT_INVENTORY_QUEUE_DLQ);

        return QueueBuilder
                .durable(SEND_EVENT_INVENTORY_QUEUE)
                .withArguments(args)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder
                .durable(SEND_EVENT_INVENTORY_QUEUE_DLQ)
                .build();
    }

    @Bean
    public FanoutExchange inventoryFanoutExchange() {
        return new FanoutExchange(publisherExchange);
    }

    @Bean
    public Binding publisherBinding(@Qualifier("publisherQueue") Queue queue, FanoutExchange inventoryFanoutExchange) {
        return BindingBuilder.bind(queue).to(inventoryFanoutExchange);
    }

}