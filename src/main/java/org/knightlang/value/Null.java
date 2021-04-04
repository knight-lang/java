package org.knightlang.value;

public final class Null extends Idempotent {
	@Override
	public void dump() {
		System.out.print("Null()");
	}

	@Override
	public long toNumber() {
		return 0;
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public boolean toBoolean() {
		return false;
	}

	@Override
	public boolean equals(Object rhs) {
		return rhs instanceof Null;
	}
}