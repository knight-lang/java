package org.knightlang.value;

import org.knightlang.exception.KnightException;

public abstract class NonIdempotent implements Value {
	@Override
	public long toLong() throws KnightException {
		return run().toLong();
	}

	@Override
	public String toString() throws KnightException {
		return run().toString();
	}

	@Override
	public boolean toBoolean() throws KnightException {
		return run().toBoolean();
	}

	@Override
	public Value add(Value rhs) {
		return run().add(rhs);
	}

	@Override
	public Value subtract(Value rhs) {
		return run().subtract(rhs);
	}

	@Override
	public Value multiply(Value rhs) {
		return run().multiply(rhs);
	}

	@Override
	public Value divide(Value rhs) {
		return run().divide(rhs);
	}

	@Override
	public Value modulo(Value rhs) {
		return run().modulo(rhs);
	}

	@Override
	public Value exponentiate(Value rhs) {
		return run().exponentiate(rhs);
	}

	@Override
	public int compareTo(Value rhs) {
		return run().compareTo(rhs.run());
	}

	@Override
	public boolean equals(Object rhs) {
		return rhs instanceof Value && run().equals(((Value) rhs).run());
	}
}