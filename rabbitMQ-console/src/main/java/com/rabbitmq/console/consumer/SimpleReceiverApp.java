package com.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SimpleReceiverApp {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory(); // фабрика, которая будет открывать соед-е с сервером rabbitmq
        factory.setHost("localhost"); // устанавливаем стандартный хост
        Connection connection = factory.newConnection(); // открываем соединение
        Channel channel = connection.createChannel(); // открыаем канал лдя работы с rabbitmq
        // любое вз-вие с сервером будет происходить через канал

        channel.queueDeclare(QUEUE_NAME, false, false, false, null); // объявляем очередь
        // queueDeclare  -  это consumer (потребитель) просит rabbitmq создать очередь для работы:
        // QUEUE_NAME -- это ее имя. А далее идут ее свойства
        System.out.println(" [*] Waiting for messages"); // печатаем, что ждем сообщение

        DeliverCallback deliverCallback = (consumerTag, delivery) -> { // delivery -  это то сообщение, кот-е попадет в очередь. Это просто пака байтов
            String message = new String(delivery.getBody(), "UTF-8"); // мы берем пачку байтов из посылки. Знаем, что будем обмениваться сообщениями в кодировке UTF-8
            // и собираем из этой посылки строку
            System.out.println(Thread.currentThread().getName() + " [x] Received '" + message + "'");
        };// это мы прописываем колл-бек. Прописываем некий листенер: когда произойдет попадание в эту очередь
        // нового сообщения, мы на него реагируем (с пом-ю колл-бека и описываем, как на него реагируем).

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        }); // вешаем этот листенер на нашу очередь. т.е. мы хотим в очереди: QUEUE_NAME  получать сообщение и
        // обрабатыватть его коллбеком: deliverCallback
    }
}
