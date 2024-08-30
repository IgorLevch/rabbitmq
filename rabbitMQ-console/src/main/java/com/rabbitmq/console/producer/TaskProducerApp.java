package com.rabbitmq.console.producer;

import com.rabbitmq.client.*;

//конфигурирование очередей и что здесь можно настроить
public class TaskProducerApp {
    private static final String TASK_EXCHANGER = "task_exchanger";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            String message = "Task.....";// формируем задачи -- отправляем строчку с 5ю точками. И на каждую точку будет уходить по секунде, т.е.всего 5 секунд
            channel.exchangeDeclare(TASK_EXCHANGER, BuiltinExchangeType.FANOUT); // создаем EXCHANGER FANOUT
            for (int i = 0; i < 20; i++) {  // закидываем сообщение Task в очередь (там будет 20 таких задачек)
                channel.basicPublish(TASK_EXCHANGER, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
}
