package com.xvhx.distributed.locks;

import java.util.List;

public interface Lock {
	/**
	 * Try to acquire the lock once.
	 *
	 * @param keys       keys to try to lock (suffix)
	 * @param storeId    lock store id to save keys in (prefix)
	 * @param expiration how long to wait before releasing the key automatically, in
	 *                   millis
	 * @return token to use for releasing the lock or {@code null} if lock cannot be
	 *         acquired at the moment
	 */
	String acquire(List<String> keys, String storeId, long expiration);

	/**
	 * Try to acquire the lock indeterminately.
	 *
	 * @param keys       keys to try to lock (suffix)
	 * @param storeId    lock store id to save keys in (prefix)
	 * @param expiration how long to wait before releasing the key automatically, in
	 *                   millis
	 * @return token to use for releasing the lock or {@code null} if lock cannot be
	 *         acquired at the moment
	 */
	String tryAcquire(List<String> keys, String storeId, long expiration);

	/**
	 * Try to releace the lock aquired using its associated token
	 * 
	 * @param keys    keys to try to unlock
	 * @param token   token used to check if lock is still held by this lock
	 * @param storeId lock store id to save keys in (table, collection, ...)
	 * @return {@code true} if lock was successfully released, {@code false}
	 *         otherwise
	 */
	boolean release(List<String> keys, String storeId, String token);
}
