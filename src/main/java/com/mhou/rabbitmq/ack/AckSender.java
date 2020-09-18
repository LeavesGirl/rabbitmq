package com.mhou.rabbitmq.ack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.mhou.rabbitmq.ack.AckConfig.*;

@RestController
@RequestMapping("/rabbitmq/ack")
@Slf4j
public class AckSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;

    @RequestMapping("/send")
    public void sendMsg(String msg, boolean ack, boolean toQueue) {
        log.info("send msg-{}", msg);
//        rabbitTemplate.convertAndSend(EXCHANGE_NAME, toQueue ? QUEUE_NAME : "A", msg, new MessagePostProcessor() {
//            @Override
//            public Message postProcessMessage(Message message) throws AmqpException {
//                return message;
//            }
//
//            @Override
//            public Message postProcessMessage(Message message, Correlation correlation) throws AmqpException {
//                message.getMessageProperties().setCorrelationId(UUID.randomUUID().toString());
//                if (correlation != null) {
//                    CorrelationData correlationData = (CorrelationData) correlation;
//                    correlationData.setId(message.getMessageProperties().getCorrelationId());
//                }
//                return message;
//            }
//        }, new CorrelationData());
        AsyncRabbitTemplate.RabbitConverterFuture<Object> future = asyncRabbitTemplate.convertSendAndReceive(EXCHANGE_NAME, toQueue ? QUEUE_NAME : "A", msg);
        future.addCallback(new ListenableFutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                log.info("callback-success:{}", result);
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.info("callback-failure:{}", throwable.getMessage());
            }
        });
    }
}
