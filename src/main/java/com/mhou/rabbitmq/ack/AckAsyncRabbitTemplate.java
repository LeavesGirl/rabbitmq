package com.mhou.rabbitmq.ack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;

@Slf4j
public class AckAsyncRabbitTemplate extends AsyncRabbitTemplate {

    public AckAsyncRabbitTemplate(RabbitTemplate template, AbstractMessageListenerContainer container) {
        super(template, container);
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息主体：{},应答码：{},应答信息：{},交换机：{},路由键：{}", new String(message.getBody()), replyCode, replyText, exchange, routingKey);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送成功：{}", correlationData);
        } else {
            log.info("消息发送失败：{}", cause);
        }
    }
}
