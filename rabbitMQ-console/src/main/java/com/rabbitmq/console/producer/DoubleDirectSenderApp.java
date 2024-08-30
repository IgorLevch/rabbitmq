package com.rabbitmq.console.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class DoubleDirectSenderApp {
    private static final String EXCHANGE_NAME = "DoubleDirect";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //в один и тот же ексченджер можно посылать сообщения с разными темами:

            channel.basicPublish(EXCHANGE_NAME, "php", null, "php msg".getBytes("UTF-8"));
            channel.basicPublish(EXCHANGE_NAME, "c++", null, "c++ msg".getBytes("UTF-8"));
            channel.basicPublish(EXCHANGE_NAME, "java", null, "java msg".getBytes("UTF-8"));
        }
    } // если хотим на ресивере получать только с 2-мя кей-пойнтами, см. ревивер.
}