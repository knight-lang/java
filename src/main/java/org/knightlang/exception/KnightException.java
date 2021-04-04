package org.knightlang.exception;

public abstract class KnightException extends RuntimeException {
	public KnightException(String msg) {
		super(msg);
	}
}