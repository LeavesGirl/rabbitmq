package com.mhou.rabbitmq.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static com.mhou.rabbitmq.exchange.MQExchangeConfig.*;

@Slf4j
public class MQExchangeConsumer {

    @RabbitHandler
    @RabbitListener(queues = {QUEUEA_NAME, QUEUEB_NAME})
    public void listener(Message message) {
        log.info("queue - {},msg - {}", message.getMessageProperties().getConsumerQueue(), new String(message.getBody()));
    }
}
