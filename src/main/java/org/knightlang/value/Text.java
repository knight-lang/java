package org.knightlang.value;

import org.knightlang.exception.RunException;

public final class Text extends Idempotent {
	private final String text;

	public Text(String text) {
		this.text = text;
	}

	@Override
	public void dump() {
		System.out.printf("String(%s)", toString());
	}

	@Override
	public long toNumber() {
		return 0;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public boolean toBoolean() {
		return !text.isEmpty();
	}

	@Override
	public boolean equals(Object rhs) {
		return rhs instanceof Text && ((Text) rhs).text == text;
	}

	@Override
	public int compareTo(Value rhs) {
		return text.compareTo(rhs.toString());
	}

	@Override
	public Value add(Value rhs) {
		return new Text(text + rhs.toString());
	}

	@Override
	public Value multiply(Value rhs) {
		String ret = "";
		long amnt = rhs.toNumber();

		if (amnt < 0) {
			throw new RunException("cannot multiply a string by a negative amount.");
		}

		while (amnt-- != 0) {
			ret += text;
		}

		return new Text(ret);
	}
}
