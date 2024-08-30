package com.rabbitmq.console.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


//конфигурирование очередей и что здесь можно настроить
public class TaskReceiverApp {
    private static final String TASK_QUEUE_NAME = "task_queue"; // на rabbitmq сервере создаем очередь
    private static final String TASK_EXCHANGER = "task_exchanger";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        // ниже мы объявили очередь:
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null); // durable true (первое true - это долговечная очередь)
        //  второе - exclusive (чтоб никто не цеплялся), третье - autodelete
        channel.queueBind(TASK_QUEUE_NAME, TASK_EXCHANGER, ""); //привязали очередь к нужному ексченжеру
        System.out.println(" [*] Waiting for messages");

        channel.basicQos(3); //указываем максимальный размер префетча: возьми три задачи, но не больше 3 задач
        // (каждый ресивер будет получать по 3 задачи )
        // если обраотка сообщений может очень сильно отличаться по времени, то тогда этот префетч лучше ставить поменьше
        // потому что какой-либо из клиентов может забрать много сложных задач и долго с ними сидеть, хотя тяжелые задачи лучше раскидывать на неск-ких испольнителей


        // что делает колл-бек:
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");// получаем сообщение из очереди

            System.out.println(" [x] Received '" + message + "'");  // печатаем, что мы получили сообщение
//            if (1 < 10) {
//                throw new RuntimeException("Oops");
//            }
            doWork(message); // ожидаем, что будет выполнена работа
            System.out.println(" [x] Done"); // и мы задачу завершим

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);// это ставим, т.к. мы выключили автоподтверждение
            // (поставили false строкой ниже)   -- и поэтому ставим basicAck. Т.е. мы подтверждаем, что все хорошо.
        };

        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
        }); // вешаем колл-бек на нашу очередь . autoack (2й параметр true/false - как только я задачу выпонил (вытащил соообщ-е из очереди)
        // -- я считаю, что оно обработано -- это если true. И мне неважно, что с ним. )
        // но т.к. есть вероятность некорректной обработки, то лучше поставить false:
        //  и когда мы будем вытаскивать сообщение , пока не отдадим подтверждение, то такая задача будет считаться временно заблокированной
        // а если подтверждение получили, значит, задача уходит из очереди . Если потр-е не получили и канал закрылся, значит, возвращаем задачу обратно в работу
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
