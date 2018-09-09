package com.example.demo.subscriber;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import com.example.demo.service.locked.LockedTask;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotationLockRedisCustomerSubscriber implements MessageListener {

	private LockedTask lockedTask;

	public AnnotationLockRedisCustomerSubscriber(LockedTask lockedTask) {
		this.lockedTask = lockedTask;
	}

	@SneakyThrows
	@Override
	public void onMessage(Message message, byte[] pattern) {

		log.info("calling execute locked task");
		lockedTask.executeLockedTask(message);
	}

}
