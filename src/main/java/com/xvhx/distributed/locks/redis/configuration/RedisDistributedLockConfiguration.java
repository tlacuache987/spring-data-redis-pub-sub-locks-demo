package com.xvhx.distributed.locks.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.xvhx.distributed.locks.Lock;
import com.xvhx.distributed.locks.redis.impl.MultiRedisLock;
import com.xvhx.distributed.locks.redis.impl.SimpleRedisLock;

@SuppressWarnings("deprecation")
@Configuration
public class RedisDistributedLockConfiguration {

	@Bean
	public Lock simpleRedisLock(final StringRedisTemplate stringRedisTemplate) {
		return new SimpleRedisLock(stringRedisTemplate);
	}

	@Bean
	public Lock multiRedisLock(final StringRedisTemplate stringRedisTemplate) {
		return new MultiRedisLock(stringRedisTemplate);
	}
}
