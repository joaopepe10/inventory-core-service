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

import static br.com.mercadolibre.core.constants.RabbitMQConstants.SEND_EVENT_INVENTORY_QUEUE;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${queue.publisher.exchange}")
    private String publisherExchange;

    @Bean
    @Qualifier("publisherQueue")
    public Queue publisherQueue() {
        return new Queue(SEND_EVENT_INVENTORY_QUEUE, true);
    }

    @Bean
    public FanoutExchange inventoryFanoutExchange() {
        return new FanoutExchange(publisherExchange);
    }

    @Bean
    public Binding publisherBinding() {
        return BindingBuilder
                .bind(publisherQueue())
                .to(inventoryFanoutExchange());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }

}