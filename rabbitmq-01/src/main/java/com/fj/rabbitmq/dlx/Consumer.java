package com.fj.rabbitmq.dlx;

import com.fj.rabbitmq.returnlistenner.MyConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;

/**
 * @author fengj
 * @date 2019/12/22 -20:46
 */
public class Consumer {

    public static void main(String[] args) throws Exception{

        // 结果：服务端的消息发送后，5秒后，test_dlx_queue中的消息没有被消费，就会将消息发送到死信队列dlx_queue中

        // 创建一个ConnectionFactory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setPort(5672);
        factory.setHost("192.168.1.112");
        factory.setVirtualHost("/");

        // 通过连接工厂创建连接
        Connection connection = factory.newConnection();
        // 通过Connection创建Channel
        Channel channel = connection.createChannel();

        // 声明普通的交换机 队列 路由
        String exchangeName = "test_dlx_exchange";
        String routeKey = "dlx.#";
        String queueName = "test_dlx_queue";

        // 声明死信队列的交换机 队列 路由
        String dlxExchangeName = "dlx_exchange";
        String dlxRouteKey = "#";
        String dlxQueueName = "dlx_queue";

        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange",dlxExchangeName);// key是固定的x-dead-letter-exchange

        channel.exchangeDeclare(exchangeName,"topic",true,false,null);
        // arguments属性要设置到声明队列上
        channel.queueDeclare(queueName,true,false,false,arguments);
        channel.queueBind(queueName,exchangeName,routeKey);

        // 死信队列的生命
        channel.exchangeDeclare(dlxExchangeName,"topic",true,false,null);
        // arguments属性要设置到声明队列上
        channel.queueDeclare(dlxQueueName,true,false,false,null);
        channel.queueBind(dlxQueueName,dlxExchangeName,dlxRouteKey);


        // 手工签收一定是在autoAck = false的情况下有效
        channel.basicConsume(queueName,false,new MyConsumer(channel));




    }
}
