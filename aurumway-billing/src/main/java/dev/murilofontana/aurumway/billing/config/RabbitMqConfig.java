package dev.murilofontana.aurumway.billing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private final String exchangeName;
    private final String queueName;
    private final String routingKey;

    public RabbitMqConfig(@Value("${messaging.payments.exchange}") String exchangeName,
                          @Value("${messaging.payments.queue}") String queueName,
                          @Value("${messaging.payments.routing-key.succeeded}") String routingKey) {
        this.exchangeName = exchangeName;
        this.queueName = queueName;
        this.routingKey = routingKey;
    }

    @Bean
    public TopicExchange paymentsEventsExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public TopicExchange paymentsEventsDlx() {
        return new TopicExchange(exchangeName + ".dlx", true, false);
    }

    @Bean
    public Queue paymentSucceededQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", exchangeName + ".dlx")
                .withArgument("x-dead-letter-routing-key", routingKey)
                .build();
    }

    @Bean
    public Queue paymentSucceededDlq() {
        return QueueBuilder.durable(queueName + ".dlq").build();
    }

    @Bean
    public Binding paymentSucceededBinding() {
        return BindingBuilder.bind(paymentSucceededQueue()).to(paymentsEventsExchange()).with(routingKey);
    }

    @Bean
    public Binding paymentSucceededDlqBinding() {
        return BindingBuilder.bind(paymentSucceededDlq()).to(paymentsEventsDlx()).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        var converter = new Jackson2JsonMessageConverter(objectMapper);
        // Cross-service messages carry the producer's class name in __TypeId__; ignore it and
        // bind to the listener method's parameter type instead.
        var typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
