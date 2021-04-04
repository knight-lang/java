package org.knightlang.value;

public abstract class Idempotent implements Value {
	@Override
	public Value run() {
		return this;
	}
}
