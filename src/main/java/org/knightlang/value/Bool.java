package org.knightlang.value;

public final class Bool extends Idempotent {
	private final boolean bool;

	public Bool(boolean bool) {
		this.bool = bool;
	}

	@Override
	public void dump() {
		System.out.printf("Boolean(%s)", toString());
	}

	@Override
	public long toNumber() {
		return bool ? 1 : 0;
	}

	@Override
	public String toString() {
		return "" + bool;
	}

	@Override
	public boolean toBoolean() {
		return bool;
	}

	@Override
	public int compareTo(Value rhs) {
		return (bool ? 1 : 0) - (rhs.toBoolean() ? 1 : 0);
	}

	@Override
	public boolean equals(Object rhs) {
		return rhs instanceof Bool && ((Bool) rhs).bool == bool;
	}
}
