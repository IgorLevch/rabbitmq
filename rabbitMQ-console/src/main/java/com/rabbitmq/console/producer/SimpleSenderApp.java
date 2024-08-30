package com.rabbitmq.console.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SimpleSenderApp {
    private final static String QUEUE_NAME = "hello"; // отправитель знает, что нужно отправлять сообщение в очередь hello
    private final static String EXCHANGER_NAME = "hello_exchanger"; // и у него есть exchanger

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();// фабрика, которая будет открывать соед-е с сервером rabbitmq
        factory.setHost("localhost");  // открываем соединение
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) { // открываем канал
            channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT); //обьявляем обменник: хотим создать на rabbitmq обменник: EXCHANGER_NAME ,
            channel.queueDeclare(QUEUE_NAME, false, false, false, null); //обьявляем очередь , котрая называется "hello"
            channel.queueBind(QUEUE_NAME, EXCHANGER_NAME, "java"); // привязываем очередь к ексченжеру с роутинг кей - java
            // т.е. мы говорим: ексченжер, ты будешь кидать в очередь сообщения с темой java

            String message = "Hello World!"; // это сообщение отправляем с продюсера
            channel.basicPublish(EXCHANGER_NAME, "java", null, message.getBytes()); // в указанный Ексченжер, с данным роутинг ки
            // message.getBytes()   -- сообщение бьем на байты и кидаем
            System.out.println(" [x] Sent '" + message + "'");  // говорим, что сообщение улетело.
        }
    }
}
























