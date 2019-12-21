package com.fj.rabbitmq.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author fengj
 * @date 2019/12/21 -13:03
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

        // 创建一个队列
        String queueName = "test001";
        channel.queueDeclare(queueName,true,false,false,null);

        // 创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        // 设置channel
        channel.basicConsume(queueName,true,queueingConsumer);

        // 获取消息
        while (true){
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();

            String s = new String(delivery.getBody());
            System.out.println("消息为：" + s);

            // 获取自定义属性
            Map<String, Object> headers = delivery.getProperties().getHeaders();

            Set<String> strings = headers.keySet();
            Iterator<String> iterator = strings.iterator();
            while (iterator.hasNext()){
                String next = iterator.next();
                Object obj = headers.get(next);
                System.out.println("自定义属性："+obj);
                iterator.remove();
            }
        }

    }
}
