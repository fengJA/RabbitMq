package com.fj.rabbitmq.limit;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author fengj
 * @date 2019/12/21 -14:17
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
        String exchangeName = "test_qos_exchange";
        String routeKey = "test.qos";


        // 通过channel发送数据
        String msg = "hello world";
        //mandatory:true表示路由不到也不会从MQ中删除，然后会调用下面的handleReturn()方法；为false则不会
        channel.basicPublish(exchangeName,routeKey,true,null,msg.getBytes());

       /* channel.close();
        connection.close();*/
    }
}
