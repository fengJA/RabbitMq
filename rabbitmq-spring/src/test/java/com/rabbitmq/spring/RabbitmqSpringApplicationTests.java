package com.rabbitmq.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.spring.entity.Order;
import com.rabbitmq.spring.entity.Packaged;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@SpringBootTest
class RabbitmqSpringApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Test
	public void testRabbit(){
		// 测试rabbitAdmin
		rabbitAdmin.declareExchange(new TopicExchange("test.topic",false,false));
		rabbitAdmin.declareExchange(new DirectExchange("test.direct",false,false));

		rabbitAdmin.declareQueue(new Queue("test.direct.queue",false));

		rabbitAdmin.declareBinding(new Binding("test.direct.queue",Binding.DestinationType.QUEUE,
				"test.direct","test.#",new HashMap<>()));

		rabbitAdmin.declareBinding(BindingBuilder
				.bind(new Queue("test.topic.queue",false))  // 直接创建队列
				.to(new TopicExchange("test.topic",false,false))  // 直接创建交换机 建立关联
				.with("topic.*")); // 创建路由键

		rabbitAdmin.declareBinding(BindingBuilder
				.bind(new Queue("test.fanout.queue",false))  // 直接创建队列
				.to(new FanoutExchange("test.fanout",false,false))); //直接创建交换机 建立关联 fanout没有路由键

		// 清空队列，false表示不需要等待
		rabbitAdmin.purgeQueue("test.topic.queue",false);
	}


	@Autowired
	private RabbitTemplate rabbitTemplate;

	// 测试rabbitTemplate
	@Test
	public void testrRabbitTemplate(){
		// 创建消息
		MessageProperties properties = new MessageProperties();
		properties.getHeaders().put("s1","sunny");
		properties.getHeaders().put("s2","fun");
		properties.setContentType("text/plain");

		Message message = new Message("hello world".getBytes(), properties);

		rabbitTemplate.convertAndSend("exchange001", "topic.save", message, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				System.out.println("添加额外的消息设置");
				message.getMessageProperties().getHeaders().put("s1","hahah");

				return message;
			}
		});

		rabbitTemplate.send("exchange001","topic.save",message);
	}

	@Test
	public void testSendJsonMessage() throws Exception {

		Order order = new Order();
		order.setId("001");
		order.setName("消息订单");
		order.setContent("描述信息");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json);

		MessageProperties messageProperties = new MessageProperties();
		//这里一定要改为application/json
		messageProperties.setContentType("application/json");
		Message message = new Message(json.getBytes(), messageProperties);

		rabbitTemplate.send("topic001", "spring.order", message);
	}


	@Test
	public void testSendJavaMessage() throws Exception {

		Order order = new Order();
		order.setId("001");
		order.setName("订单消息");
		order.setContent("订单描述信息");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json);

		MessageProperties messageProperties = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties.setContentType("application/json");
		messageProperties.getHeaders().put("__TypeId__", "com.bfxy.spring.entity.Order");
		Message message = new Message(json.getBytes(), messageProperties);

		rabbitTemplate.send("topic001", "spring.order", message);
	}

	@Test
	public void testSendMappingMessage() throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Order order = new Order();
		order.setId("001");
		order.setName("订单消息");
		order.setContent("订单描述信息");

		String json1 = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json1);

		MessageProperties messageProperties1 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties1.setContentType("application/json");
		messageProperties1.getHeaders().put("__TypeId__", "order");
		Message message1 = new Message(json1.getBytes(), messageProperties1);
		rabbitTemplate.send("topic001", "spring.order", message1);

		Packaged pack = new Packaged();
		pack.setId("002");
		pack.setName("包裹消息");
		pack.setDescription("包裹描述信息");

		String json2 = mapper.writeValueAsString(pack);
		System.err.println("pack 4 json: " + json2);

		MessageProperties messageProperties2 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties2.setContentType("application/json");
		messageProperties2.getHeaders().put("__TypeId__", "packaged");
		Message message2 = new Message(json2.getBytes(), messageProperties2);
		rabbitTemplate.send("topic001", "spring.pack", message2);
	}

	@Test
	public void testSendExtConverterMessage() throws Exception {
//			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "picture.png"));
//			MessageProperties messageProperties = new MessageProperties();
//			messageProperties.setContentType("image/png");
//			messageProperties.getHeaders().put("extName", "png");
//			Message message = new Message(body, messageProperties);
//			rabbitTemplate.send("", "image_queue", message);

		byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "mysql.pdf"));
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("application/pdf");
		Message message = new Message(body, messageProperties);
		rabbitTemplate.send("", "pdf_queue", message);
	}


}
