package com.fj.rabbitmq.limit;

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
        String exchangeName = "test_return_exchange";
        String routeKey = "test.#";
        String queueName = "test_return_queue";

        channel.exchangeDeclare(exchangeName,"topic",true,false,null);
        channel.queueDeclare(queueName,true,false,false,null);
        channel.queueBind(queueName,exchangeName,routeKey);

        // 0：表示对消息大小没有限制；1：最多消费端过来多少(1)条消息，我ACk后在把其他消息(1条1条)发给我
        // false:表示不应用到channel级别，应用到consumer端
        channel.basicQos(0,1,false);


        // 设置channel
        channel.basicConsume(queueName,true,new MyConsumer(channel));




    }
}
