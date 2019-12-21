package com.fj.rabbitmq.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author fengj
 * @date 2019/12/21 -13:50
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
        String exchangeName = "test_confirm_exchange";
        String routeKey = "test.confirm";
        String queueName = "test_confirm_queue";

        channel.exchangeDeclare(exchangeName,"topic",true,false,null);
        channel.queueDeclare(queueName,true,false,false,null);
        channel.queueBind(queueName,exchangeName,routeKey);

        // 创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        // 设置channel
        channel.basicConsume(queueName,true,queueingConsumer);

        // 获取消息
        while (true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();

            String s = new String(delivery.getBody());
            System.out.println("消息为：" + s);
        }




    }
}
