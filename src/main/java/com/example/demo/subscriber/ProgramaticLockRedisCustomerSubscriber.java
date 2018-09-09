package com.example.demo.subscriber;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.example.demo.model.Customer;
import com.xvhx.distributed.locks.Lock;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProgramaticLockRedisCustomerSubscriber implements MessageListener {

	private RedisTemplate<String, Object> redisTemplate;

	private Lock simpleRedisLock;

	@Value("${com.xvhx.distributedlocks.storeId:locks}")
	private String storeId;

	@Value("${com.xvhx.distributedlocks.expiration:5000}")
	private Long expiration;

	private List<String> lockKeys = Arrays.asList("distributedLock");

	private @Setter boolean keepTryingAcquireLock = false;

	public ProgramaticLockRedisCustomerSubscriber(RedisTemplate<String, Object> redisTemplate, Lock simpleRedisLock) {
		this.redisTemplate = redisTemplate;
		this.simpleRedisLock = simpleRedisLock;
	}

	@SneakyThrows
	@Override
	public void onMessage(Message message, byte[] pattern) {

		String token = acquireLock(keepTryingAcquireLock, lockKeys, storeId, expiration);

		Customer customer = (Customer) redisTemplate.getValueSerializer().deserialize(message.getBody());
		log.info("Message received: Customer id {}", customer.getId());

		// TODO remove for production
		Thread.sleep(1200);

		simpleRedisLock.release(lockKeys, storeId, token);
	}

	private String acquireLock(boolean keepTryingAcquireLock, List<String> lockKeys, String storeId, long expiration) {

		String token = null;

		if (!keepTryingAcquireLock) {

			token = simpleRedisLock.acquire(lockKeys, storeId, expiration);

			// check if current thread, JVM acquired a token
			if (StringUtils.isEmpty(token)) {
				throw new IllegalStateException("Lock not acquired!");
			}

		} else {

			token = simpleRedisLock.tryAcquire(lockKeys, storeId, expiration);

		}

		return token;
	}

}
