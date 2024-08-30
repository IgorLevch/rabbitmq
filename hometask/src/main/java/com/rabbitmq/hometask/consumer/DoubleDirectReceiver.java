package com.rabbitmq.hometask.consumer;

import com.rabbitmq.client.*;

public class DoubleDirectReceiver {
    private static final String EXCHANGE_NAME = "DoubleDirect";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT); // объявляем exchanger

        String queueName = channel.queueDeclare().getQueue(); //создаем для себя временную очередь
        System.out.println("My queue name: " + queueName);

        channel.queueBind(queueName, EXCHANGE_NAME, "php"); // мы говорим, что если придет сообщение с темой php,то кидайте мне это сообщение
        channel.queueBind(queueName, EXCHANGE_NAME, "java");//  и если придет соощение с темой java, то тоже кидайте в эту очередь
        //выше создаем 2 роутинга(потому что 2 кей-пойнта , а не 3)

        System.out.println(" [*] Waiting for messages");
        // а на стороне потребителя поток только один:
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            System.out.println(Thread.currentThread().getName());
        };  // см. сендера: если нам нужно принимать только 2 кей-понйта, а не 3 (как в сендере), то создаем все равно 1 колл-бек.
        // но создаем 2 роутинга (см. выше)

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }


}
