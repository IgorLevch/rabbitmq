package com.rabbitmq.console.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ExchangeSenderApp {
    private static final String EXCHANGE_NAME = "directExchanger";// наш EXCHANGER

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory(); // создаем factory
        factory.setHost("localhost");  // делаем host
        try (Connection connection = factory.newConnection(); // подключаемся
             Channel channel = connection.createChannel()) { // открываем канал
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT); // объявляем  DIRECT EXCHANGER
            String message = "Hello World!"; // посылаем сообщение
            channel.basicPublish(EXCHANGE_NAME, "php", null, message.getBytes("UTF-8")); // в этот EXCHANGER кидаем сообщения с темой php
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}

// очередь создает ресивер, а не сендер, т.к. может быть много ресиверов