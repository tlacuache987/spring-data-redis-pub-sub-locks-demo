package com.xvhx.distributed.locks.redis.impl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.xvhx.distributed.locks.Lock;
import com.xvhx.distributed.locks.exception.DistributedLockException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@AllArgsConstructor
public class SimpleRedisLock implements Lock {
	private static final String LOCK_SCRIPT = "return redis.call('SET', KEYS[1], ARGV[1], 'PX', tonumber(ARGV[2]), 'NX') and true or false";
	private static final String LOCK_RELEASE_SCRIPT = "return redis.call('GET', KEYS[1]) == ARGV[1] and (redis.call('DEL', KEYS[1]) == 1) or false";

	private final RedisScript<Boolean> lockScript = new DefaultRedisScript<>(LOCK_SCRIPT, Boolean.class);
	private final RedisScript<Boolean> lockReleaseScript = new DefaultRedisScript<>(LOCK_RELEASE_SCRIPT, Boolean.class);

	private final StringRedisTemplate stringRedisTemplate;
	private final Supplier<String> tokenSupplier;

	public SimpleRedisLock(final StringRedisTemplate stringRedisTemplate) {
		this(stringRedisTemplate, () -> UUID.randomUUID().toString());
	}

	@Override
	public String acquire(final List<String> keys, final String storeId, final long expiration) {
		Assert.isTrue(keys.size() == 1, "Cannot acquire lock for multiple keys with this lock: " + keys);

		final String key = keys.get(0);
		final List<String> singletonKeyList = Collections.singletonList(storeId + ":" + key);
		final String token = tokenSupplier.get();

		if (StringUtils.isEmpty(token)) {
			throw new DistributedLockException("Cannot lock with empty token");
		}

		final boolean locked = stringRedisTemplate.execute(lockScript, singletonKeyList, token,
				String.valueOf(expiration));
		log.trace("Tried to acquire lock for key {} with token {} in store {}. Locked: {}", key, token, storeId,
				locked);

		if (locked) {
			log.debug("Lock acquired!");
		}
		return locked ? token : null;
	}

	@SneakyThrows
	@Override
	public String tryAcquire(final List<String> keys, final String storeId, final long expiration) {
		String token = null;
		while (true) {
			token = this.acquire(keys, storeId, expiration);

			// check if current thread, JVM acquired a token
			if (!StringUtils.isEmpty(token)) {
				break;
			} else {
				Thread.sleep(200);
			}
		}
		return token;
	}

	@Override
	public boolean release(final List<String> keys, final String storeId, final String token) {
		Assert.isTrue(keys.size() == 1, "Cannot release lock for multiple keys with this lock: " + keys);
		final String key = keys.get(0);

		final List<String> singletonKeyList = Collections.singletonList(storeId + ":" + key);

		final boolean released = stringRedisTemplate.execute(lockReleaseScript, singletonKeyList, token);
		if (released) {
			log.trace("Release script deleted the record for key {} with token {} in store {}", key, token, storeId);
			log.debug("Lock released !");
		} else {
			log.trace("Release script failed for key {} with token {} in store {}", key, token, storeId);
			log.debug("Lock cannot released !");
		}
		return released;
	}
}