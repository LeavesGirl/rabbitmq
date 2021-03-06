package com.mhou.rabbitmq.delay;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;
import java.util.Date;

import static com.mhou.rabbitmq.delay.DelayMQConfig.DEADLETTER_QUEUE_NAME;

@Slf4j
public class DelayConsumer {

    @RabbitHandler
    @RabbitListener(queues = DEADLETTER_QUEUE_NAME)
    public void receiveA0(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        log.info("当前时间：{},死信队列收到消息：{}", new Date().toString(), msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
