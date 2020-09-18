package com.mhou.rabbitmq.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mhou.rabbitmq.exchange.MQExchangeConfig.*;

@RestController
@Slf4j
public class MQExchangeSender {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendDiffExchange")
    public void sendDiffExchange(String exchangeType, String msg) {
        log.info("msg-{}", msg);
        switch (exchangeType) {
            case "f":
                //routingkey的指定没有实际意义，可以删除
                rabbitTemplate.convertSendAndReceive(FANOUT_EXCHANGE_NAME, "", msg + "a");
                rabbitTemplate.convertSendAndReceive(FANOUT_EXCHANGE_NAME, "", msg + "b");
                break;
            case "c":
                rabbitTemplate.convertSendAndReceive(CUSTOM_EXCHANGE_NAME, ROUTING_KEY_A_NAME, msg + "a", message -> {
                    message.getMessageProperties().setDelay(10000);
                    return message;
                });
                rabbitTemplate.convertSendAndReceive(CUSTOM_EXCHANGE_NAME, ROUTING_KEY_B_NAME, msg + "b", message -> {
                    message.getMessageProperties().setHeader("x-delay", 20000);
                    return message;
                });
                break;
            case "h":
                rabbitTemplate.convertSendAndReceive(HEADERS_EXCHANGE_NAME, "", msg + "a", message -> {
                    message.getMessageProperties().setHeader("HeaderA", "aaa");
                    return message;
                });
                rabbitTemplate.convertSendAndReceive(HEADERS_EXCHANGE_NAME, "", msg + "b", message -> {
                    message.getMessageProperties().setHeader("HeaderA", "aaa");
                    message.getMessageProperties().setHeader("HeaderB", "bbb");
                    return message;
                });
                break;
            case "t":
                rabbitTemplate.convertSendAndReceive(TOPIC_EXCHANGE_NAME, ROUTING_KEY_A_NAME + "." + ROUTING_KEY_B_NAME, msg + "a");
                rabbitTemplate.convertSendAndReceive(TOPIC_EXCHANGE_NAME, ROUTING_KEY_A_NAME + "." + ROUTING_KEY_B_NAME, msg + "b");
                break;
            default:
                rabbitTemplate.convertSendAndReceive(DIRECT_EXCHANGE_NAME, ROUTING_KEY_A_NAME, msg + "a");
                rabbitTemplate.convertSendAndReceive(DIRECT_EXCHANGE_NAME, ROUTING_KEY_B_NAME, msg + "b");
        }
    }
}
