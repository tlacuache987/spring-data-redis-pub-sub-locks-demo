package com.xvhx.distributed.locks.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xvhx.distributed.locks.Lock;
import com.xvhx.distributed.locks.redis.aspects.LockedAspect;

@Configuration
public class RedisAnnotationAspectsDistributedLockConfiguration {

	@Bean
	public LockedAspect lockedAspect(Lock simpleRedisLock) {
		return new LockedAspect(simpleRedisLock);
	}

}
