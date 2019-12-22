package com.fj.rabbitmq.ack;

import com.fj.rabbitmq.returnlistenner.MyConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author fengj
 * @date 2019/12/22 -20:46
 */
public class Consumer {

    public static void main(String[] args) throws Exception{

        // 创建一个ConnectionFactory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("192.168.1.112");
        factory.setVirtualHost("/");

        // 通过连接工厂创建连接
        Connection connection = factory.newConnection();
        // 通过Connection创建Channel
        Channel channel = connection.createChannel();

        // 声明
        String exchangeName = "test_ack_exchange";
        String routeKey = "ack.#";
        String queueName = "test_ack_queue";

        channel.exchangeDeclare(exchangeName,"topic",true,false,null);
        channel.queueDeclare(queueName,true,false,false,null);
        channel.queueBind(queueName,exchangeName,routeKey);



        // 手工签收一定是在autoAck = false的情况下有效
        channel.basicConsume(queueName,false,new MyConsumer(channel));




    }
}
