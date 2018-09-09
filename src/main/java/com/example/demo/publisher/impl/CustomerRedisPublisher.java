package com.example.demo.publisher.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import com.example.demo.model.Customer;
import com.example.demo.publisher.CustomerPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerRedisPublisher implements CustomerPublisher {

	private RedisTemplate<String, Object> redisTemplate;

	private ChannelTopic customerChannel;

	public CustomerRedisPublisher(RedisTemplate<String, Object> redisTemplate,
			ChannelTopic customerChannel) {
		this.redisTemplate = redisTemplate;
		this.customerChannel = customerChannel;
	}

	@Override
	public void publish(Customer customer) {
		log.info("Sending message: Customer id {} ", customer.getId());
		redisTemplate.convertAndSend(customerChannel.getTopic(), customer);
	}

}
