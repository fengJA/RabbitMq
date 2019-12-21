package com.fj.rabbitmq.topicexchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.sun.org.apache.xpath.internal.operations.Div;

/**
 * @author fengj
 * @date 2019/12/21 -11:40
 */
public class Consumer {

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.112");
        factory.setPort(5672);
        factory.setVirtualHost("/");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_topic_exchange";
        String exchangeType = "topic";
        String queueName = "test_topic_queue";
        String routingKey = "user.#";// #匹配一个或者多个词
        String routingKey1 = "user.*";// *匹配一个词


        channel.exchangeDeclare(exchangeName,exchangeType,true,false,null);
        channel.queueDeclare(queueName,true,false,false,null);
        channel.queueBind(queueName,exchangeName,routingKey);

        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        String s = channel.basicConsume(queueName, true, queueingConsumer);

        while (true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            byte[] body = delivery.getBody();
            String s1 = new String(body);
            System.out.println(s1);
        }
    }
}
