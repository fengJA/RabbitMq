package com.rabbitmq.spring.adaptor;

import com.rabbitmq.spring.entity.Order;
import com.rabbitmq.spring.entity.Packaged;

import java.io.File;
import java.util.Map;

/**
 * @author fengj
 * @date 2019/12/22 -23:47
 */
public class MessageAdaptor {

    // 方法名是固定的handlerMessage，MessageListenerAdapter类中的规则：ORIGINAL_DEFAULT_LISTENER_METHOD = "handleMessage";
    public void handlerMessage(byte[] messageBody){
        System.out.println("默认方法，消息内容" + new String(messageBody));
    }

    //将方法名改为了listenerAdapter.setDefaultListenerMethod("consumerMessage");则不调用上面的方法，调用下面的
    public void consumerMessage(byte[] messageBody){
        System.out.println("默认方法，消息内容" + new String(messageBody));
    }

    // 形参改为String类型
    public void consumerMessage(String messageBody){
        System.out.println("默认方法，消息内容" + new String(messageBody));
    }

    public void method1(String messageBody){
        System.out.println("默认方法，消息内容" + new String(messageBody));
    }
    public void method2(String messageBody){
        System.out.println("默认方法，消息内容" + new String(messageBody));
    }

    // json格式
    public void consumeMessage(Map messageBody) {
        System.err.println("map方法, 消息内容:" + messageBody);
    }


    public void consumeMessage(Order order) {
        System.err.println("order对象, 消息内容, id: " + order.getId() +
                ", name: " + order.getName() +
                ", content: "+ order.getContent());
    }

    public void consumeMessage(Packaged pack) {
        System.err.println("package对象, 消息内容, id: " + pack.getId() +
                ", name: " + pack.getName() +
                ", content: "+ pack.getDescription());
    }

    public void consumeMessage(File file) {
        System.err.println("文件对象 方法, 消息内容:" + file.getName());
    }


}
