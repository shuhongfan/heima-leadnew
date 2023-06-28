package com.heima.listener;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HelloListener {

    @KafkaListener(topics = "itcast-topic")
    public void onMessage(String message) {
        if (!StringUtils.isEmpty(message)) {
            System.out.println(message);
        }
    }
}
