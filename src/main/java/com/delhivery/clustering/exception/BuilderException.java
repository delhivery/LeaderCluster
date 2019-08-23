package com.delhivery.clustering.exception;

/**
 * @author Shiv Krishna Jaiswal
 */
public final class BuilderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BuilderException(String message) {
		super(message);
	}

	public BuilderException(Throwable e) {
		super(e);
	}
}
