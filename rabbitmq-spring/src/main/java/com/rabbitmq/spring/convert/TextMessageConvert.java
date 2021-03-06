package com.rabbitmq.spring.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author fengj
 * @date 2019/12/23 -0:00
 */
public class TextMessageConvert implements MessageConverter {

    // 将Java对象转换为Message对象
    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(o.toString().getBytes(),messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        String contentType = message.getMessageProperties().getContentType();
        if (null != contentType && contentType.contains("text")){
            return new String(message.getBody());
        }
        return message.getBody();
    }
}
