package com.fj.rabbitmq.topicexchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author fengj
 * @date 2019/12/21 -11:57
 */
public class Producer {

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

        String exchangeName = "test_topic_exchange";
        String routingKey = "user.topic";
        String routingKey1 = "user.save";
        String routingKey2 = "user.update";
        String routingKey3 = "user.delete.test";

        // 通过channel发送数据
        String msg = "hello world";
        channel.basicPublish(exchangeName,routingKey,null,msg.getBytes());
        channel.basicPublish(exchangeName,routingKey1,null,msg.getBytes());
        channel.basicPublish(exchangeName,routingKey2,null,msg.getBytes());
        channel.basicPublish(exchangeName,routingKey3,null,msg.getBytes());

        channel.close();
        connection.close();
    }
}
