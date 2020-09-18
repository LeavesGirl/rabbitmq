package com.mhou.rabbitmq.ack;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;

import static com.mhou.rabbitmq.ack.AckConfig.QUEUE_NAME;

@Slf4j
public class AckConsumer {
    int index = 1;

    @RabbitListener(queues = QUEUE_NAME)
    public void consume(Message message, Channel channel) {
        String s = new String(message.getBody());
        log.info("msg-{}", s);
        try {
            if ("ack".equals(s)) {
//                //手动确认消息已消费，可以从队列删除
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else if ("requeue".equals(s) && index < 5) {
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //重新入队列，测试结果看，后面的消息会被先消费，所以应该是排到队列尾部的
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                log.info("requeue:{}", index);
                index++;
            } else {
                //不重新入队列，直接删除
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                index = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
