package com.xvhx.distributed.locks.redis.aspects;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StringUtils;

import com.xvhx.distributed.locks.Lock;
import com.xvhx.distributed.locks.Locked;
import com.xvhx.distributed.locks.redis.alias.RedisLocked;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class LockedAspect {

	private Lock simpleRedisLock;

	private List<String> lockKeys = Arrays.asList("distributedLock");

	public LockedAspect(Lock simpleRedisLock) {
		this.simpleRedisLock = simpleRedisLock;
	}

	@Around(value = "@annotation(redisLocked)")
	public Object beforeLockedTaskMethod(ProceedingJoinPoint pjp, RedisLocked redisLocked) throws Throwable {

		log.trace("Starting Redis locking throught aspects.");

		Object obj = null;
		String token = null;

		boolean keepTryingAcquireLock = false;

		try {

			Locked.Type type = redisLocked.lockedType();

			keepTryingAcquireLock = Locked.Type.KEEP_TRYING.equals(type) ? true : false;

			token = acquireLock(keepTryingAcquireLock, lockKeys, redisLocked.storeId(), redisLocked.expiration());

			log.trace("Proceeding to target method.");
			obj = pjp.proceed();

		} catch (Exception ex) {
			log.trace("Exception thrown.");
			throw ex;
		} finally {
			if (token != null) {
				simpleRedisLock.release(lockKeys, redisLocked.storeId(), token);
			}
		}

		log.trace("Redis locking throught aspects ending.");
		return obj;
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
