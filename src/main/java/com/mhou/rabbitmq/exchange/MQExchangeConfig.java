package com.mhou.rabbitmq.exchange;

import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "rabbitmq.test.exchange")
public class MQExchangeConfig {

    public static final String DIRECT_EXCHANGE_NAME = "direct-Exchange";
    public static final String FANOUT_EXCHANGE_NAME = "fanout-Exchange";
    public static final String CUSTOM_EXCHANGE_NAME = "custom-Exchange";
    public static final String HEADERS_EXCHANGE_NAME = "headers-Exchange";
    public static final String TOPIC_EXCHANGE_NAME = "topic-Exchange";
    public static final String QUEUEA_NAME = "queueA";
    public static final String QUEUEB_NAME = "queueB";
    public static final String ROUTING_KEY_A_NAME = "routingKeyA";
    public static final String ROUTING_KEY_B_NAME = "routingKeyB";


    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NAME);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    @Bean
    CustomExchange customExchange() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(CUSTOM_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    HeadersExchange headersExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("HeaderA", "aaa");
        args.put("HeaderB", "bbb");
        return (HeadersExchange) ExchangeBuilder.headersExchange(HEADERS_EXCHANGE_NAME).withArguments(args).build();
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    Queue queueA() {
        return new Queue(QUEUEA_NAME);
    }

    @Bean
    Queue queueB() {
        return new Queue(QUEUEB_NAME);
    }

    @Bean
    Binding bindingAD(Queue queueA, DirectExchange directExchange) {
        return BindingBuilder.bind(queueA).to(directExchange).with(ROUTING_KEY_A_NAME);
    }

    @Bean
    Binding bindingAF(Queue queueA, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueA).to(fanoutExchange);
    }

    @Bean
    Binding bindingAC(Queue queueA, CustomExchange customExchange) {
        return BindingBuilder.bind(queueA).to(customExchange).with(ROUTING_KEY_A_NAME).noargs();
    }

    @Bean
    Binding bindingAH(Queue queueA, HeadersExchange headersExchange) {
        //精确匹配
        // return BindingBuilder.bind(queueA).to(headersExchange).where("Header").matches("ccc");
        //部分匹配
        return BindingBuilder.bind(queueA).to(headersExchange).whereAny("HeaderA", "HeaderB").exist();
    }

    @Bean
    Binding bindingAT(Queue queueA, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueA).to(topicExchange).with("*." + ROUTING_KEY_A_NAME);
    }

    @Bean
    Binding bindingBD(Queue queueB, DirectExchange directExchange) {
        return BindingBuilder.bind(queueB).to(directExchange).with(ROUTING_KEY_B_NAME);
    }

    @Bean
    Binding bindingBF(Queue queueB, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueB).to(fanoutExchange);
    }

    @Bean
    Binding bindingBC(Queue queueB, CustomExchange customExchange) {
        return BindingBuilder.bind(queueB).to(customExchange).with(ROUTING_KEY_B_NAME).noargs();
    }

    @Bean
    Binding bindingBH(Queue queueB, HeadersExchange headersExchange) {
        //全部匹配
        return BindingBuilder.bind(queueB).to(headersExchange).whereAll("HeaderA", "HeaderB").exist();
    }

    @Bean
    Binding bindingBT(Queue queueB, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueB).to(topicExchange).with("#." + ROUTING_KEY_B_NAME);
    }

    //先初始化队列
    @Bean
    @ConditionalOnBean(Queue.class)
    MQExchangeConsumer mqExchangeConsumer() {
        return new MQExchangeConsumer();
    }

}
