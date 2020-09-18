package com.mhou.rabbitmq.ack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.Exchange;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty("rabbitmq.test.ack")
@Slf4j
public class AckConfig {


    public static final String QUEUE_NAME = "queueAck";
    public static final String EXCHANGE_NAME = "exchangeAck";

    @Bean
    Queue queue() {
        Map<String, Object> map = new HashMap<>();
        return new Queue(QUEUE_NAME);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

//    @RabbitListener(bindings = @QueueBinding(key = QUEUE_NAME, exchange = @Exchange(EXCHANGE_NAME), value = @org.springframework.amqp.rabbit.annotation.Queue(QUEUE_NAME)))
//    public void receiveMsg(String msg) {
//        log.info("receive msg-{}", msg);
//    }

    @Bean
    Binding binding(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).withQueueName();
    }

    @Bean
    @ConditionalOnBean(Queue.class)
    AckConsumer ackConsumer() {
        return new AckConsumer();
    }


    @Bean
    AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate, Queue queue) {
        SimpleMessageListenerContainer listener = new SimpleMessageListenerContainer(rabbitTemplate.getConnectionFactory());
        listener.setQueues(queue);
        listener.setMaxConcurrentConsumers(3);
        listener.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        AsyncRabbitTemplate asyncRabbitTemplate = new AckAsyncRabbitTemplate(rabbitTemplate, listener);
        //异步确认时使用下面的设置方式，AsyncRabbitTemplate本身同时实现了ReturnCallBack和ConfirmCallBack接口
        //确认
        asyncRabbitTemplate.setEnableConfirms(true);
        //消息返回
        asyncRabbitTemplate.setMandatory(true);
        return asyncRabbitTemplate;
    }

}
