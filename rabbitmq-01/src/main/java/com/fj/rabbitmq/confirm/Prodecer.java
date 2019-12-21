package com.fj.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * @author fengj
 * @date 2019/12/21 -13:54
 */
public class Prodecer {

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

        // 指定消息的投递模式：confirm模式
        channel.confirmSelect();

        // 声明
        String exchangeName = "test_confirm_exchange";
        String routeKey = "test.confirm";

        // 通过channel发送数据
        String msg = "hello world";
        channel.basicPublish(exchangeName,routeKey,null,msg.getBytes());

        // 添加一个确认监听
        channel.addConfirmListener(new ConfirmListener() {
            // 确认失败调用的方法
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {

            }

            // 成功调用的方法
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {

            }
        });

       /* channel.close();
        connection.close();*/
    }
}
