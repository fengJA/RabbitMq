package com.rabbit.springboot.rabbitspringboot;

import com.rabbit.springboot.rabbitspringboot.entity.Order;
import com.rabbit.springboot.rabbitspringboot.producer.RabbitmqSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class RabbitSpringbootApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private RabbitmqSender rabbitSender;

	// SimpleDateFormat:不是线程安全的
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	@Test
	public void testSender1() throws Exception {
		Map<String, Object> properties = new HashMap<>();
		properties.put("number", "12345");
		properties.put("send_time", simpleDateFormat.format(new Date()));
		rabbitSender.send("Hello RabbitMQ For Spring Boot!", properties);
	}

	@Test
	public void testSender2() throws Exception {
		Order order = new Order("001", "第一个订单");
		rabbitSender.sendOrder(order);

	}
}
