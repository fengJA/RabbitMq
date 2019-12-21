package com.fj.rabbitmq.returnlistenner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * @author fengj
 * @date 2019/12/21 -14:17
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
        String exchangeName = "test_return_exchange";
        String routeKey = "test.#";
        String queueName = "test_return_queue";

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
