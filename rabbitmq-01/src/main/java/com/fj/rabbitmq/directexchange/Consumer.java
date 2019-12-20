package com.fj.rabbitmq.directexchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * @author fengj
 * @date 2019/12/20 -23:22
 */
public class Consumer {
    public static void main(String[] args) throws Exception{

        // 创建一个ConnectionFactory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.112");
        factory.setPort(5672);
        factory.setVirtualHost("/");

        // 通过连接工厂创建连接
        Connection connection = factory.newConnection();

        // 通过Connection创建Channel
        Channel channel = connection.createChannel();

        // 声明
        String exchangeName = "test_direct_exchange";
        String exchangeType = "direct";
        String routeKey = "test.direct";
        String queueName = "test_direct_queue";

        // 声明一个交换机
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,false,null);
        // 创建一个队列
        channel.queueDeclare(queueName,false,false,false,null);
        //绑定队列
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
