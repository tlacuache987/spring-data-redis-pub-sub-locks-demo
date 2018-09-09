package com.xvhx.distributed.locks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Locked {

	public static enum Type {
		TRY_ONCE, KEEP_TRYING
	}

	/**
	 * Id of a specific store for lock to use.
	 * For JDBC, this would be a lock table.
	 * For Mongo, this would be a collection name.
	 * For Redis, this would be a prefix
	 */
	String storeId() default "locks";

	/**
	 * Keys to lock.
	 */
	String[] keys() default { "distributedLockForAnnotation" };

	/**
	 * Lock expiration in milliseconds.
	 */
	long expiration() default 5000L;

	/**
	 * Locked Type.
	 */
	Locked.Type lockedType() default Locked.Type.KEEP_TRYING;

	/**
	 * Lock type, see implementations of {@link Lock}.
	 */
	Class<? extends Lock> type() default Lock.class;
}