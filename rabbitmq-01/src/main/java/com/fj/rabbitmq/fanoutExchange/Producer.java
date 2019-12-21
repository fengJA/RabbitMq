package com.fj.rabbitmq.fanoutExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author fengj
 * @date 2019/12/21 -12:43
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

        String exchangeName = "test_fanout_exchange";
        String routingKey = "user.fanout"; // 生产者随便设置一个或不设置路由键，消费者都可以获取消息

        // 通过channel发送数据
        String msg = "hello world";
        channel.basicPublish(exchangeName,routingKey,null,msg.getBytes());


        channel.close();
        connection.close();
    }
}
