package com.example.demo.service.locked.impl;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.demo.model.Customer;
import com.example.demo.service.locked.LockedTask;
import com.xvhx.distributed.locks.Locked.Type;
import com.xvhx.distributed.locks.redis.alias.RedisLocked;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TryOnceLockedTask implements LockedTask {

	private RedisTemplate<String, Object> redisTemplate;

	public TryOnceLockedTask(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@SneakyThrows
	@Override
	@RedisLocked(storeId = "locks", keys = "distributedLockForAnnotation", lockedType = Type.TRY_ONCE, expiration = 5000L)
	public void executeLockedTask(Object input) {

		Message message = (Message) input;

		Customer customer = (Customer) redisTemplate.getValueSerializer().deserialize(message.getBody());
		log.info("Message received: Customer id {}", customer.getId());

		// TODO remove for production
		Thread.sleep(1200);
	}

}
