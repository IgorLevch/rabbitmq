package com.rabbitmq.spring.from_console_to_web;

import org.springframework.stereotype.Component;

@Component
public class SimpleMessageReceiver {
    public void receiveMessage(byte[] message) {  // получаем байтовый массив
        System.out.println("Received from topic <" + new String(message) + ">"); // и преобразовываем его к строке
    }
}