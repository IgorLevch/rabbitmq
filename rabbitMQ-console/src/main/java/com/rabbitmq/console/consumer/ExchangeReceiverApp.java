package com.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

public class ExchangeReceiverApp {
    private static final String EXCHANGE_NAME = "directExchanger"; //Exchanger

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory(); // делаем фабрику
        factory.setHost("localhost");  // делаем хост
        Connection connection = factory.newConnection();  // подключаемся
        Channel channel = connection.createChannel(); // открываем канал

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);// на всяк. случай проверяем, что у нас есть
        // DIRECT EXCHANGER

        String queueName = channel.queueDeclare().getQueue(); //  channel.queueDeclare() -- канал, создай очередь. getQueue() - дай нам это имя
        System.out.println("My queue name: " + queueName); // печатаем, что это за очередь

        channel.queueBind(queueName, EXCHANGE_NAME, "php");
        // сразу же делаем бинд: очередь создали, присвоили ей имя, и говорим: свяжись с нашим обменником

        System.out.println(" [*] Waiting for messages");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        }; // создали коллбек


        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}

// очередь создает ресивер, а не сендер, т.к. может быть много ресиверов