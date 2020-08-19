package com.mhou.rabbitmq.delay;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * Description:  延时队列配置
 *
 * @author houmingye（minguye.hou01@ucarinc.com）
 * @version 1.0
 * @date 2020-08-17 14:27
 */
@Configuration
public class DelayMQConfig {

    // 业务队列交换机
    public static final String BUSINESS_EXCHANGE_NAME = "business.exchange";
    // 业务队列
    public static final String BUSINESS_QUEUE_NAME = "business.queue";
    //业务路由key
    public static final String BUSINESS_ROUTING_KEY = "business.routingKey";
    // 死信队列交换机
    public static final String DEADLETTER_EXCHANGE_NAME = "deadLetter.exchange";
    // 死信队列
    public static final String DEADLETTER_QUEUE_NAME = "deadLetter.queue";
    // 死信路由key
    public static final String DEADLETTER_ROUTING_KEY = "deadLetter.routingKey";

    /**
     * decription: 业务队列使用交换机
     */

    @Bean
    DirectExchange businessExchange() {
        return new DirectExchange(BUSINESS_EXCHANGE_NAME);
    }

    /**
     * decription: 业务队列
     */
    @Bean
    Queue businessQueue() {
        //业务队列绑定死信交换机,设置路由键,设置TTL
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", DEADLETTER_EXCHANGE_NAME);
        map.put("x-dead-letter-routing-key", DEADLETTER_ROUTING_KEY);
        map.put("x-message-ttl", 6000);
//        map.put("x-expires", 10000);
        return QueueBuilder.durable(BUSINESS_QUEUE_NAME).withArguments(map).build();
    }

    /**
     * decription:死信队列使用交换机
     */

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEADLETTER_EXCHANGE_NAME);
    }

    /**
     * decription: 死信队列
     */
    @Bean
    Queue deadLetterQueue() {
        return new Queue(DEADLETTER_QUEUE_NAME);
    }

    /**
     * decription: 业务队列绑定业务交换机
     */
    @Bean
    Binding businessBinding(DirectExchange businessExchange, Queue businessQueue) {
        return BindingBuilder.bind(businessQueue).to(businessExchange).with(BUSINESS_ROUTING_KEY);
    }

    /**
     * decription: 死信队列绑定死信交换机
     */
    @Bean
    Binding deadLetterBinding(DirectExchange deadLetterExchange, Queue deadLetterQueue) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DEADLETTER_ROUTING_KEY);
    }
}
