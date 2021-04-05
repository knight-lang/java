package org.knightlang.value;

import org.knightlang.exception.RunException;

public interface Value extends Comparable<Value> {
	public Value run() throws RunException;
	public void dump();

	public long toLong() throws RunException;
	public String toString() throws RunException;
	public boolean toBoolean() throws RunException;

	public default Value add(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot add");
	}

	public default Value subtract(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot subtract");
	}

	public default Value multiply(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot multiply");
	}

	public default Value divide(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot divide");
	}

	public default Value modulo(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot modulo");
	}

	public default Value exponentiate(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot exponentiate");
	}

	public default int compareTo(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot compare");
	}
}
