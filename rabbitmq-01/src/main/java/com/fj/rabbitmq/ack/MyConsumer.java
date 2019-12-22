package com.fj.rabbitmq.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * @author fengj
 * @date 2019/12/22 -20:32
 */
public class MyConsumer extends DefaultConsumer {
    private Channel channel;
    public MyConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        System.out.println("Consumer Message.....");

        if("1".equals(properties.getHeaders().get("myObj"))){
            // multiple：false表示不是批量的      requeue:false表示不重回队列，即不重新发送
            channel.basicNack(envelope.getDeliveryTag(),false,false);
        }else {
            // envelope.getDeliveryTag()：取到消息的标签     false:表示不批量接受
            channel.basicAck(envelope.getDeliveryTag(),false);
        }
    }
}
