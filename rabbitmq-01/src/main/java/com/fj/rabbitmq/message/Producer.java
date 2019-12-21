package com.fj.rabbitmq.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;

/**
 * @author fengj
 * @date 2019/12/21 -12:52
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

        HashMap<String, Object> header = new HashMap<>();
        header.put("myObj","111");
        header.put("myObj2","222");

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .contentEncoding("UTF-8")
                .expiration("5000")
                .deliveryMode(2)  // 2：表示持久化，1：表示不持久化，即服务重启，没有被消费的消息就没了
                .headers(header)  // 设置自定义属性
                .build();


        // 通过channel发送数据
        String msg = "hello world";
        for (int i = 0; i < 5; i++) {

            channel.basicPublish("","test001",properties,msg.getBytes());
        }

        channel.close();
        connection.close();
    }
}
