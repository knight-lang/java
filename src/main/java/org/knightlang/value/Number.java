package org.knightlang.value;

import org.knightlang.exception.RunException;

public final class Number extends Idempotent {
	private final long number;

	public Number(long number) {
		this.number = number;
	}

	@Override
	public void dump() {
		System.out.printf("Number(%l)", number);
	}

	@Override
	public long toLong() {
		return number;
	}

	@Override
	public String toString() {
		return "" + number;
	}

	@Override
	public boolean toBoolean() {
		return number != 0;
	}

	@Override
	public Value add(Value rhs) {
		return new Number(number + rhs.toLong());
	}

	@Override
	public Value subtract(Value rhs) {
		return new Number(number - rhs.toLong());
	}

	@Override
	public Value multiply(Value rhs) {
		return new Number(number * rhs.toLong());
	}

	@Override
	public Value divide(Value rhs) {
		long rnum = rhs.toLong();

		if (rnum == 0) {
			throw new RunException("cannot divide by zero");
		}

		return new Number(number / rnum);
	}

	@Override
	public Value modulo(Value rhs) {
		long rnum = rhs.toLong();

		if (rnum == 0) {
			throw new RunException("cannot modulo by zero");
		}

		return new Number(number % rnum);
	}

	@Override
	public Value exponentiate(Value rhs) {
		throw new RunException("todo: exponentiate");
	}

	@Override
	public int compareTo(Value rhs) {
		return Long.compare(number, rhs.toLong());
	}

	@Override
	public boolean equals(Object rhs) {
		return rhs instanceof Number && ((Number) rhs).number == number;
	}
}
