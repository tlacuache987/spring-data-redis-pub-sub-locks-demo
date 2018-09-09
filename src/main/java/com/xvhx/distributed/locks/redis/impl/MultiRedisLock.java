package com.xvhx.distributed.locks.redis.impl;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.StringUtils;

import com.xvhx.distributed.locks.Lock;
import com.xvhx.distributed.locks.exception.DistributedLockException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Data
@Slf4j
@AllArgsConstructor
public class MultiRedisLock implements Lock {
	private static final String LOCK_SCRIPT = "local msetnx_keys_with_tokens = {}\n" +
			"for _, key in ipairs(KEYS) do\n" +
			"    msetnx_keys_with_tokens[#msetnx_keys_with_tokens + 1] = key\n" +
			"    msetnx_keys_with_tokens[#msetnx_keys_with_tokens + 1] = ARGV[1]\n" +
			"end\n" +
			"local keys_successfully_set = redis.call('MSETNX', unpack(msetnx_keys_with_tokens))\n" +
			"if (keys_successfully_set == 0) then\n" +
			"    return false\n" +
			"end\n" +
			"local expiration = tonumber(ARGV[2])\n" +
			"for _, key in ipairs(KEYS) do\n" +
			"    redis.call('PEXPIRE', key, expiration)\n" +
			"end\n" +
			"return true\n";

	private static final String LOCK_RELEASE_SCRIPT = "for _, key in pairs(KEYS) do\n" +
			"    if redis.call('GET', key) ~= ARGV[1] then\n" +
			"        return false\n" +
			"    end\n" +
			"end\n" +
			"redis.call('DEL', unpack(KEYS))\n" +
			"return true\n";

	private final RedisScript<Boolean> lockScript = new DefaultRedisScript<>(LOCK_SCRIPT, Boolean.class);
	private final RedisScript<Boolean> lockReleaseScript = new DefaultRedisScript<>(LOCK_RELEASE_SCRIPT, Boolean.class);

	private final StringRedisTemplate stringRedisTemplate;
	private final Supplier<String> tokenSupplier;

	public MultiRedisLock(final StringRedisTemplate stringRedisTemplate) {
		this(stringRedisTemplate, () -> UUID.randomUUID().toString());
	}

	@Override
	public String acquire(final List<String> keys, final String storeId, final long expiration) {
		final List<String> keysWithStoreIdPrefix = keys.stream().map(key -> storeId + ":" + key)
				.collect(Collectors.toList());
		final String token = tokenSupplier.get();

		if (StringUtils.isEmpty(token)) {
			throw new DistributedLockException("Cannot lock with empty token");
		}

		final Boolean locked = stringRedisTemplate.execute(lockScript, keysWithStoreIdPrefix, token,
				String.valueOf(expiration));
		log.trace("Tried to acquire lock for keys '{}' in store '{}' with safety token '{}'. Locked: {}", keys, storeId,
				token, locked);

		if (locked) {
			log.debug("Lock acquired!");
		}
		return locked ? token : null;
	}

	@SneakyThrows
	@Override
	public String tryAcquire(List<String> keys, String storeId, long expiration) {
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
		final List<String> keysWithStoreIdPrefix = keys.stream().map(key -> storeId + ":" + key)
				.collect(Collectors.toList());

		final boolean released = stringRedisTemplate.execute(lockReleaseScript, keysWithStoreIdPrefix, token);
		if (released) {
			log.trace("Release script deleted the record for keys {} with token {} in store {}", keys, token, storeId);
			log.debug("Lock released !");
		} else {
			log.trace("Release script failed for keys {} with token {} in store {}", keys, token, storeId);
			log.debug("Lock cannot released !");
		}
		return released;
	}
}
