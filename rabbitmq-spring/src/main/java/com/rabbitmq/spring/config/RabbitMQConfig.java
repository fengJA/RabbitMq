package com.rabbitmq.spring.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.spring.adaptor.MessageAdaptor;
import com.rabbitmq.spring.convert.ImageMessageConverter;
import com.rabbitmq.spring.convert.PDFMessageConverter;
import com.rabbitmq.spring.convert.TextMessageConvert;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author fengj
 * @date 2019/12/22 -22:07
 */
@Configuration
@ComponentScan("com.rabbitmq.spring.*")
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setAddresses("192.168.1.112:5672");
        cachingConnectionFactory.setHost("/");
        cachingConnectionFactory.setUsername("gust");
        cachingConnectionFactory.setPassword("guest");

        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);  // 一定为true

        return rabbitAdmin;
    }

    // 直接创建队列，交换机
    @Bean
    public TopicExchange exchange001(){
        return new TopicExchange("exchange001",true,false);
    }

    @Bean
    public Queue queue001(){
        return new Queue("queue001",true);
    }

    @Bean
    public Queue queue002(){
        return new Queue("queue002",true);
    }

    @Bean
    public Binding bind001(){
        return BindingBuilder.bind(queue001()).to(exchange001()).with("topic.#");
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.addQueues(queue001(),queue002());// 可以同时监听多个队列
        container.setConcurrentConsumers(1);// 设置当前用户数
        container.setMaxConcurrentConsumers(5);// 最大用户数
        container.setAcknowledgeMode(AcknowledgeMode.AUTO); // 自动签收
        container.setDefaultRequeueRejected(false);// 不重回队列
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID(); // 给队列加一个标签
            }
        });

        // 有消息过来就会监听，调用onMessage方法
        // 方法一：
        /*container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                String s = new String(message.getBody());
                System.out.println("消费者的消息"+s);
            }
        });*/

        // 方法二：适配器模式
        /*MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new MessageAdaptor());
        listenerAdapter.setDefaultListenerMethod("consumerMessage");

        listenerAdapter.setMessageConverter(new TextMessageConvert());

        container.setMessageListener(listenerAdapter);*/

        // 2.适配器方式：队列名，方法名也可以一一匹配
        /*MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new MessageAdaptor());
        listenerAdapter.setMessageConverter(new TextMessageConvert());
        Map<String,String> queueOrTagMethodName = new HashMap<>();
        queueOrTagMethodName.put("queue001","method1");
        queueOrTagMethodName.put("queue002","method2");
        listenerAdapter.setQueueOrTagToMethodName(queueOrTagMethodName);
        container.setMessageListener(listenerAdapter);*/


        // 1.1 支持json格式的转换器
        /* MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageAdaptor());
         adapter.setDefaultListenerMethod("consumeMessage");

         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         adapter.setMessageConverter(jackson2JsonMessageConverter);

         container.setMessageListener(adapter);*/


         // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
        /**
         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageAdaptor());
         adapter.setDefaultListenerMethod("consumeMessage");

         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

         adapter.setMessageConverter(jackson2JsonMessageConverter);
         container.setMessageListener(adapter);
         */


        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
        /**
         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageAdaptor());
         adapter.setDefaultListenerMethod("consumeMessage");
         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

         Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
         idClassMapping.put("order", com.bfxy.spring.entity.Order.class);
         idClassMapping.put("packaged", com.bfxy.spring.entity.Packaged.class);

         javaTypeMapper.setIdClassMapping(idClassMapping);

         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
         adapter.setMessageConverter(jackson2JsonMessageConverter);
         container.setMessageListener(adapter);
         */

        //1.4 ext convert

        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageAdaptor());
        adapter.setDefaultListenerMethod("consumeMessage");

        //全局的转换器:
        ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();

        TextMessageConvert textConvert = new TextMessageConvert();
        convert.addDelegate("text", textConvert);
        convert.addDelegate("html/text", textConvert);
        convert.addDelegate("xml/text", textConvert);
        convert.addDelegate("text/plain", textConvert);

        Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
        convert.addDelegate("json", jsonConvert);
        convert.addDelegate("application/json", jsonConvert);

        ImageMessageConverter imageConverter = new ImageMessageConverter();
        convert.addDelegate("image/png", imageConverter);
        convert.addDelegate("image", imageConverter);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();
        convert.addDelegate("application/pdf", pdfConverter);


        adapter.setMessageConverter(convert);
        container.setMessageListener(adapter);

        return container;

    }

}
