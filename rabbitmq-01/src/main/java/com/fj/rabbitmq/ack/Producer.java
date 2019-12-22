package com.fj.rabbitmq.ack;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;

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
        String exchangeName = "test_ack_exchange";
        String routeKey = "ack.save";


        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> header = new HashMap<>();
            header.put("myObj",i);

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .contentEncoding("UTF-8")
                    .expiration("5000")
                    .deliveryMode(2)  // 2：表示持久化，1：表示不持久化，即服务重启，没有被消费的消息就没了
                    .headers(header)  // 设置自定义属性
                    .build();


            // 通过channel发送数据
            String msg = "hello world" + i;
            //mandatory:true表示路由不到也不会从MQ中删除，然后会调用下面的handleReturn()方法；为false则不会
            channel.basicPublish(exchangeName,routeKey,true,properties,msg.getBytes());
        }


       /* channel.close();
        connection.close();*/
    }
}
