package com.rabbitmq.console.consumer;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class HomeTaskReceiver {

    private static final String EXCHANGE_NAME ="task_exchanger";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localHost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        String queuName = channel.queueDeclare().getQueue();
        System.out.println("My queue name:  "+queuName);

        DeliverCallback deliveryCallback = (consumerTag, delivery) ->{

            String message = new String(delivery.getBody(),"UTF-8");
            System.out.println("[x] Topic'"+delivery.getEnvelope().getRoutingKey() + "'");
            System.out.println("[x] Msg" + message + "'");
            System.out.println("[x] ___________________");
            };

        channel.basicConsume(queuName, true, deliveryCallback, consumerTag ->{});
       Scanner scanner = new Scanner(System.in);
       while(true){
           String msg = scanner.nextLine();
           String[] msgArr = msg.split(" ",2);
           if (msgArr.length>1 & msgArr[0].equals("set_topic")){
               channel.queueBind(queuName, EXCHANGE_NAME, msgArr[1]);
               System.out.println("[x] you add topic '" + msgArr[1]+"'");
           }

           if (msgArr.length>1 & msgArr[0].equals("del_topic")){
               channel.queueUnbind(queuName, EXCHANGE_NAME, msgArr[1]);
               System.out.println("[x] you delete topic '" + msgArr[1]+" '");
           }

           if (msg.equals("bye")){

               scanner.close();
               channel.close();
               return;
           }


        }





    }




}
