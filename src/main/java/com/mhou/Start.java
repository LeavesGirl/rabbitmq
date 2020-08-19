
package com.mhou;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Description:  TODO
 *
 * @author houmingye（minguye.hou01@ucarinc.com）
 * @version 1.0
 * @date 2020-08-17 14:27
 */
@SpringBootApplication
@EnableRabbit
public class Start {
    public static void main(String[] args) {
        SpringApplication.run(Start.class);
    }
}
