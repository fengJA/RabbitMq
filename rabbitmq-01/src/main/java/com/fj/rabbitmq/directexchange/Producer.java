package com.fj.rabbitmq.directexchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author fengj
 * @date 2019/12/20 -23:23
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

        // 声明
        String exchangeName = "test_direct_exchange";
        String routeKey = "test.direct";

        // 通过channel发送数据
        String msg = "hello world";
        channel.basicPublish(exchangeName,routeKey,null,msg.getBytes());

        channel.close();
        connection.close();
    }
}
