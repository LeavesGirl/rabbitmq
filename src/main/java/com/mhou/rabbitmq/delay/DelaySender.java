package com.mhou.rabbitmq.delay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mhou.rabbitmq.delay.DelayMQConfig.*;

@RestController
@Slf4j
public class DelaySender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("sendDirectMessage")
    public void sendMsg(String msg, Integer time) {
        log.info("send msg:{},time:{}", msg, time);
        if (time == null) {
            rabbitTemplate.convertAndSend(BUSINESS_EXCHANGE_NAME, BUSINESS_ROUTING_KEY, msg);
        } else {
            //测试过期时间，理论上取较小时间
            rabbitTemplate.convertAndSend(BUSINESS_EXCHANGE_NAME, BUSINESS_ROUTING_KEY, msg, a -> {
                a.getMessageProperties().setExpiration(time + "");
                return a;
            });
        }
    }


}
