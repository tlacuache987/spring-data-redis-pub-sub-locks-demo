package com.xvhx.distributed.locks.exception;

public class DistributedLockException extends RuntimeException {

	private static final long serialVersionUID = 2187099792114841113L;

	public DistributedLockException(final String message) {
		super(message);
	}

	public DistributedLockException(final String message, final Throwable e) {
		super(message, e);
	}
}