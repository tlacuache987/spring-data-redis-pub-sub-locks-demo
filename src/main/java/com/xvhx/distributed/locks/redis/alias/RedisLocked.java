package com.xvhx.distributed.locks.redis.alias;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.xvhx.distributed.locks.Locked;
import com.xvhx.distributed.locks.redis.impl.SimpleRedisLock;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Locked(type = SimpleRedisLock.class)
public @interface RedisLocked {

	@AliasFor(annotation = Locked.class)
	String storeId() default "locks";

	@AliasFor(annotation = Locked.class)
	String[] keys() default { "distributedLockForAnnotation" };

	@AliasFor(annotation = Locked.class)
	long expiration() default 5000L;

	@AliasFor(annotation = Locked.class)
	Locked.Type lockedType() default Locked.Type.KEEP_TRYING;
}
