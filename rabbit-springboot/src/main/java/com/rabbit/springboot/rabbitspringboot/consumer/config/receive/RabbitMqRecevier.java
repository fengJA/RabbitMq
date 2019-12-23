package com.rabbit.springboot.rabbitspringboot.consumer.config.receive;

import com.rabbit.springboot.rabbitspringboot.entity.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author fengj
 * @date 2019/12/23 -14:07
 * 应该将生产端和消费端分两个模块，类路径一致（即Order实体类）
 */
@Component
public class RabbitMqRecevier {


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "test-queue",durable = "true"),
    exchange = @Exchange(value = "test-exchange",durable = "true",type = "topic",ignoreDeclarationExceptions = "true"),
    key = "springbot.*"))
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception{
        System.out.println("消费端收到的消息：" + message.getPayload());

        Long deliverTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        // 手工ack
        channel.basicAck(deliverTag,false);// 不批量处理
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
            durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
            key = "${spring.rabbitmq.listener.order.key}"))
    // 将Message拆分为两部分：@Payload  @Headers，即用Java类传递消息
    @RabbitHandler
    public void onOrderMessage(@Payload Order order, Channel channel, @Headers Map<String,Object> headers) throws Exception{
        System.out.println("消费端Order：" + order.getId());
        Long deliverTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliverTag,false);
    }
}
